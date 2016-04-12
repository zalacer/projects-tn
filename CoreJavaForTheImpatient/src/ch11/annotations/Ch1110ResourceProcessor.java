package ch11.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Scanner;

//10. Implement a processor for the @Resource annotation that accepts an object of
//some class and looks for fields of type String annotated with
//@Resource(name="URL"). Then load the URL and “inject” the string variable
//with that content, using reflection.

public class Ch1110ResourceProcessor {
  
  @Target(FIELD)
  @Retention(RUNTIME)
  public @interface Resource {
    String name() default "";
  }
  
  public static class Demo {
    @Resource(name="http://www.horstmann.com/") String field1;
    @Resource(name="http://www.danielolof.se/") int field2;
    @Resource(name="http://www.garylemasson.com/") String field3;
    String field4;
    double field5;
    
    Demo() {}
  }

  public static void processResource(Object o) {
    Class<? extends Object> cl = o.getClass();
    Field[] fields = cl.getDeclaredFields();
    for (Field f : fields) {
      if (f.getType() == String.class) {
        Resource a = f.getAnnotation(Resource.class);
        if (a != null) {
          String url = a.name();
          String contents = getPage(url);
          try {
            f.set(o, contents);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
  
  final static String lineSep = System.getProperty("line.separator");

  public static String getPage(String urlString) {
    StringBuilder builder = new StringBuilder();
    try {
      Scanner in = new Scanner(new URL(urlString).openStream());
      while (in.hasNextLine()) {
        builder.append(in.nextLine());
        builder.append(lineSep);
      }
      in.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return builder.toString();
  }

  public static void main(String[] args) throws InterruptedException {
    
    Demo d = new Demo();
    
    System.out.println("before Resource processing:");
    System.out.println(d.field1);
    System.out.println(d.field2);
    System.out.println(d.field3);
    System.out.println(d.field4);
    System.out.println(d.field5);
//    before Resource processing:
//      null
//      0
//      null
//      null
//      0.0

    processResource(d);
    Thread.sleep(5);
    
    System.out.println("\nafter Resource processing:");
    System.out.println(d.field1.length());
    System.out.println(d.field2);
    System.out.println(d.field3.length());
    System.out.println(d.field4);
    System.out.println(d.field5);
//    after Resource processing:
//      7331
//      0
//      9464
//      null
//      0.0

    System.out.println(d.field3);
    
//    <!DOCTYPE html>
//    <!--[if IEMobile 7]><html class="iem7"  lang="fr" dir="ltr"><![endif]-->
//    <!--[if lte IE 6]><html class="lt-ie9 lt-ie8 lt-ie7"  lang="fr" dir="ltr"><![endif]-->
//    <!--[if (IE 7)&(!IEMobile)]><html class="lt-ie9 lt-ie8"  lang="fr" dir="ltr"><![endif]-->
//    <!--[if IE 8]><html class="lt-ie9"  lang="fr" dir="ltr"><![endif]-->
//    <!--[if (gte IE 9)|(gt IEMobile 7)]><!--><html  lang="fr" dir="ltr" prefix="fb: http://ogp.me/ns/fb# og: http://ogp.me/ns# article: http://ogp.me/ns/article# book: http://ogp.me/ns/book# profile: http://ogp.me/ns/profile# video: http://ogp.me/ns/video# product: http://ogp.me/ns/product#"><!--<![endif]-->
//
//    <head>
//      <meta charset="utf-8" />
//    <link rel="shortcut icon" href="http://www.garylemasson.com/sites/default/files/favicon_0.ico" type="image/vnd.microsoft.icon" />
//    <meta name="description" content="Are you really looking for a SEO consultant Freelance in Paris ? and on Google by the way ? This Google Resume might convince you" />
//    <meta name="keywords" content="Gary Le Masson, Google Resume, CV Google" />
//    <meta name="generator" content="Drupal 7 (http://drupal.org)" />
//    <link rel="image_src" href="http://www.garylemasson.com/sites/all/themes/zengary2/gary-le-masson.png" />
//    <meta name="rights" content="Gary Le Masson 2013" />
//    <link rel="canonical" href="http://www.garylemasson.com" />
//    <meta property="og:site_name" content="My Google Resume - Mon CV Google" />
//    <meta property="og:type" content="website" />
//    <meta property="og:url" content="http://www.garylemasson.com" />
//    <meta property="og:title" content="Google Resume - CV Google" />
//    <meta property="og:description" content="Welcome on Gary Le Masson&#039;s Google Resume ! A French SEO expert specialised in UX and Webmarketing." />
//    <meta property="og:image" content="http://www.garylemasson.com/sites/all/themes/zengary2/gary-le-masson.png" />
//      <title>Gary Le Masson - SEO Consultant freelance in Paris</title>
//
//        <!--[if IEMobile]><meta http-equiv="cleartype" content="on"><![endif]-->
//
//      <style>
//    @import url("http://www.garylemasson.com/modules/system/system.base.css?nz34dn");
//    @import url("http://www.garylemasson.com/modules/system/system.messages.css?nz34dn");
//    @import url("http://www.garylemasson.com/modules/system/system.theme.css?nz34dn");
//    </style>
//    <style>
//    @import url("http://www.garylemasson.com/modules/field/theme/field.css?nz34dn");
//    @import url("http://www.garylemasson.com/modules/node/node.css?nz34dn");
//    @import url("http://www.garylemasson.com/modules/search/search.css?nz34dn");
//    @import url("http://www.garylemasson.com/modules/user/user.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/modules/views/css/views.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/modules/ckeditor/css/ckeditor.css?nz34dn");
//    </style>
//    <style>
//    @import url("http://www.garylemasson.com/sites/all/modules/ctools/css/ctools.css?nz34dn");
//    </style>
//    <style>
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/normalize.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/wireframes.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/layouts/fixed-width.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/tabs.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/pages.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/blocks.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/navigation.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/views-styles.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/nodes.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/comments.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/forms.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/fields.css?nz34dn");
//    @import url("http://www.garylemasson.com/sites/all/themes/zengary2/css/print.css?nz34dn");
//    </style>
//      <script src="http://www.garylemasson.com/misc/jquery.js?v=1.4.4"></script>
//    <script src="http://www.garylemasson.com/misc/jquery.once.js?v=1.2"></script>
//    <script src="http://www.garylemasson.com/misc/drupal.js?nz34dn"></script>
//    <script src="http://www.garylemasson.com/sites/default/files/languages/fr_2xop8JiSTM9b2AttZ4sHszg-u-7-MNeGHf4ZQjbtlbw.js?nz34dn"></script>
//    <script>jQuery.extend(Drupal.settings, {"basePath":"\/","pathPrefix":"fr\/","ajaxPageState":{"theme":"zengary2","theme_token":"4dMiHxaKltMiwZspaniumX-TOS2unUhx1DvbqhPgNH4","js":{"misc\/jquery.js":1,"misc\/jquery.once.js":1,"misc\/drupal.js":1,"public:\/\/languages\/fr_2xop8JiSTM9b2AttZ4sHszg-u-7-MNeGHf4ZQjbtlbw.js":1},"css":{"modules\/system\/system.base.css":1,"modules\/system\/system.menus.css":1,"modules\/system\/system.messages.css":1,"modules\/system\/system.theme.css":1,"modules\/field\/theme\/field.css":1,"modules\/node\/node.css":1,"modules\/search\/search.css":1,"modules\/user\/user.css":1,"sites\/all\/modules\/views\/css\/views.css":1,"sites\/all\/modules\/ckeditor\/css\/ckeditor.css":1,"sites\/all\/modules\/ctools\/css\/ctools.css":1,"sites\/all\/themes\/zengary2\/system.menus.css":1,"sites\/all\/themes\/zengary2\/css\/normalize.css":1,"sites\/all\/themes\/zengary2\/css\/wireframes.css":1,"sites\/all\/themes\/zengary2\/css\/layouts\/fixed-width.css":1,"sites\/all\/themes\/zengary2\/css\/page-backgrounds.css":1,"sites\/all\/themes\/zengary2\/css\/tabs.css":1,"sites\/all\/themes\/zengary2\/css\/pages.css":1,"sites\/all\/themes\/zengary2\/css\/blocks.css":1,"sites\/all\/themes\/zengary2\/css\/navigation.css":1,"sites\/all\/themes\/zengary2\/css\/views-styles.css":1,"sites\/all\/themes\/zengary2\/css\/nodes.css":1,"sites\/all\/themes\/zengary2\/css\/comments.css":1,"sites\/all\/themes\/zengary2\/css\/forms.css":1,"sites\/all\/themes\/zengary2\/css\/fields.css":1,"sites\/all\/themes\/zengary2\/css\/print.css":1}}});</script>
//      </head>
//    <body class="html front not-logged-in no-sidebars page-node page-node- page-node-15 node-type-page i18n-fr" >
//    <!-- Google Tag Manager -->
//    <noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-KTSHNP" height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
//    <script type="text/javascript">(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0];var j=d.createElement(s);var dl=l!='dataLayer'?'&l='+l:'';j.src='//www.googletagmanager.com/gtm.js?id='+i+dl;j.type='text/javascript';j.async=true;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','GTM-KTSHNP');</script>
//    <!-- End Google Tag Manager -->
//          <p id="skip-link">
//          <a href="#main-menu" class="element-invisible element-focusable">Jump to navigation</a>
//        </p>
//          
//    <div id="page">
//
//      <header id="header" role="banner">
//
//        
//        
//        
//        
//      </header>
//
//      <div id="main">
//
//        
//        
//
//
//        <div id="content" class="column" role="main">
//                      <a id="main-content"></a>
//                        <h1 class="title" id="page-title">CV Google</h1>
//                                              
//
//
//    <article class="node-15 node node-page view-mode-full clearfix">
//
//      
//      <div class="field field-name-body field-type-text-with-summary field-label-hidden"><div class="field-items"><div class="field-item even"><div id="fb-root"> </div>
//    <div class="rtecenter">
//    <p><img alt="gary-le-masson" src="http://www.garylemasson.com/sites/all/themes/zengary2/gary_le_masson.png" style="margin-top:146px" /></p>
//    <p>This is not a search engine - This is just Gary Le Masson's Google Resume, your future SEO Consultant</p>
//    <div id=" boutons">
//    <div id="bouton"><a onclick="dataLayer.push({'event':'gaEvent', 'eventCategory':'Homepage Buttons', 'eventAction':'CV EN', 'eventLabel':'internal Click'});" href="http://www.garylemasson.com/en/gary-le-masson">Google Resume in english</a></div>
//    <div id="bouton"><a onclick="dataLayer.push({'event':'gaEvent', 'eventCategory':'Homepage Buttons', 'eventAction':'CV FR', 'eventLabel':'internal Click'});" href="http://www.garylemasson.com/fr/gary-le-masson">CV Google en français</a></div>
//    </div>
//    <div id="share-boutons">
//    <p> </p>
//    </div>
//    <p> </p>
//    </div>
//    <p> </p>
//    </div></div></div>
//      
//      
//    </article>
//              </div><!-- /#content -->
//
//        <div id="navigation">
//
//                  <nav id="main-menu" role="navigation">
//              <h2 class="element-invisible">Menu principal</h2><ul class="links inline clearfix"><li class="menu-332 first"><a href="https://plus.google.com/+garylemasson" title="">Google+</a></li>
//    <li class="menu-334"><a href="http://fr.twitter.com/garylemasson" title="">Twitter</a></li>
//    <li class="menu-331"><a href="https://www.linkedin.com/in/garylemasson" title="">Linkedin</a></li>
//    <li class="menu-333"><a href="http://fr.viadeo.com/fr/profile/gary.le-masson" title="">Viadeo</a></li>
//    <li class="menu-336"><a href="http://pinterest.com/garylemasson/" title="">Pinterest</a></li>
//    <li class="menu-1107 last"><a href="https://plus.google.com/communities/102813641560589817522" title="">Cercle SEO</a></li>
//    </ul>        </nav>
//          
//          
//        </div><!-- /#navigation -->
//
//
//
//      </div><!-- /#main -->
//
//      
//    </div><!-- /#page -->
//
//      </body>
//    </html>
  }

}
