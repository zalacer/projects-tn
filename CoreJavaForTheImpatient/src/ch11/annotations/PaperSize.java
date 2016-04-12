package ch11.annotations;

public enum PaperSize {
  ISO_4A0("ISO 216 4A0", "mm", 1632, 2378), ISO_2A0("ISO 216 2A0", "mm", 1189, 1682), ISO_A0("ISO 216 A0", "mm", 841,
      1189);
  final String info;
  final String unit;
  final double width;
  final double height;

  PaperSize(String info, String unit, double width, double height) {
    this.info = info;
    this.unit = unit;
    this.width = width;
    this.height = height;
  }
}
