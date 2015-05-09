package tn

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio._
import scala.concurrent.duration._
import akka.actor._
import akka.routing.RoundRobinPool
import akka.util._
import akka.event.Logging.Info

object InvertImageAkka extends App {

  sealed trait IMessage
  case object Process extends IMessage
  case class Fabricate(img: BufferedImage, partition: (Int, Int)) extends IMessage
  case class Result(a: Array[(Int, Int, Int)]) extends IMessage
  case class Final(b: BufferedImage, duration: Duration, ncores: Int) extends IMessage

  class Omni(img: BufferedImage, partitions: IndexedSeq[(Int, Int)], ncores: Int, bravo: ActorRef)
    extends Actor with ActorLogging {
    var dest: BufferedImage =
      new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB)
    var nresults = 0
    val start: Long = System.currentTimeMillis
    var elapsed: Duration = start.millis
    val fabRouter = context.actorOf(RoundRobinPool(ncores).props(Props[Invert]), "fabRouter")
    def receive = {
      case Process => for (i <- 1 to ncores) fabRouter ! Fabricate(img, partitions(i - 1))
      case Result(a) =>
        for (i <- 0 to (a.length - 1)) dest.setRGB(a(i)._1, a(i)._2, a(i)._3)
        nresults += 1
        if (nresults == ncores) {
          elapsed = (System.currentTimeMillis - start).millis
          bravo ! Final(dest, elapsed, ncores)
          context.stop(self)
        }
    }
  }

  class Invert extends Actor with ActorLogging {
    def invert(img: BufferedImage, partition: (Int, Int)): Array[(Int, Int, Int)] = {
      val plen = partition._2 - partition._1
      val h = img.getHeight()
      val w = img.getWidth()
      var rgb, red, green, blue, inverted = 0
      val a = new Array[(Int, Int, Int)](plen * w)
      var i = 0
      for (y <- partition._1 until partition._2) {
        for (x <- 0 until w) {
          rgb = img.getRGB(x, y)
          red = 0xFF - ((rgb >> 16) & 0xFF)
          green = 0xFF - ((rgb >> 8) & 0xFF)
          blue = 0xFF - (rgb & 0xFF)
          inverted = (0xFF << 24) + (red << 16) + (green << 8) + blue
          a(i) = (x, y, inverted)
          i += 1
        }
      }
      a
    }

    def receive = {
      case Fabricate(b, p) =>
        val s = sender
        s ! Result(invert(b, p))
    }
  }

  class Bravo extends Actor with ActorLogging {
    def receive = {
      case Final(image, duration, ncores) =>
        println("" + ncores + "-threaded time: " + duration)
        writeImage(image, imageout)
        context.system.shutdown
    }
  }

  def readImage(f: String): BufferedImage = {
    var img: BufferedImage = null;
    try {
      img = ImageIO.read(new File(f))
    } catch {
      case e: IOException =>
        println("IOException while reading image")
        e.getMessage
    }
    img
  }

  def writeImage(b: BufferedImage, f: String): Unit = {
    try {
      val outputfile = new File(f);
      ImageIO.write(b, "png", outputfile);
    } catch {
      case e: IOException =>
        println("IOException while writing image")
        e.getMessage
    }
  }

  def checkImgFile(file: String): String = {
    val f = new File(file)
    if (!f.exists())
      return ("does not exist")
    if (!f.canRead())
      return ("is not readable")
    try {
      val img = ImageIO.read(f)
    } catch {
      case e: Exception =>
        return ("caused " + e.getClass() + " on read attempt")
    }
    return ("ok");
  }

  def partitionBIarray(img: BufferedImage, ncores: Int): IndexedSeq[(Int, Int)] = {
    // divides img array vertically into ncores partitions with close to equal height ranges
    val h = img.getHeight()
    val splits = (0 to ncores).map { j => math.round((j.toDouble * h) / ncores).toInt }
    val partitions = for (i <- 0 to (splits.length - 2)) yield (splits(i), splits(i + 1))
    partitions
  }

  def checkArgs: Unit = {
    val usage = "usage: InvertImage -help | imageIn imageOut numberOfCores"

    if (args.length < 3) { println(usage); System.exit(0) }

    for (i <- 0 to (args.length - 1))
      if ((args(i) == "-help") || (args(i) == "--help")) { println(usage); System.exit(0) }

    if (!args(2).matches("[1-9][0-9]*")) {
      println(usage)
      println("numberOfCores must be a positive integer")
      System.exit(1)
    }

    val checkImgOut = checkImgFile(args(0))
    if (checkImgOut != "ok") { println(args(0) + " " + checkImgOut); System.exit(1) }

    imagein = args(0)
    imageout = args(1)
    ncores = args(2).toInt

  }

  def process(ncores: Int) {
    require(ncores > 0, "number of cores must be > 0")
    val img = readImage(imagein)
    val parts = partitionBIarray(img, ncores)
    val system = ActorSystem("ImageProcessingSystem")
    val bravo = system.actorOf(Props[Bravo], name = "bravo")
    val omni = system.actorOf(Props(new Omni(img, parts, ncores, bravo)), name = "omni")
    omni ! Process
  }

  var imagein = ""
  var imageout = ""
  var ncores = 0
  checkArgs
  process(ncores)

}
