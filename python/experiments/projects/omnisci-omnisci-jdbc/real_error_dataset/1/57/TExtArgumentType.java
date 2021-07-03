/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.omnisci.thrift.calciteserver;


public enum TExtArgumentType implements org.apache.thrift.TEnum {
  Int8(0),
  Int16(1),
  Int32(2),
  Int64(3),
  Float(4),
  Double(5),
  Void(6),
  PInt8(7),
  PInt16(8),
  PInt32(9),
  PInt64(10),
  PFloat(11),
  PDouble(12),
  PBool(13),
  Bool(14),
  ArrayInt8(15),
  ArrayInt16(16),
  ArrayInt32(17),
  ArrayInt64(18),
  ArrayFloat(19),
  ArrayDouble(20),
  ArrayBool(21),
  GeoPoint(22),
  GeoLineString(23),
  Cursor(24),
  GeoPolygon(25),
  GeoMultiPolygon(26);

  private final int value;

  private TExtArgumentType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static TExtArgumentType findByValue(int value) { 
    switch (value) {
      case 0:
        return Int8;
      case 1:
        return Int16;
      case 2:
        return Int32;
      case 3:
        return Int64;
      case 4:
        return Float;
      case 5:
        return Double;
      case 6:
        return Void;
      case 7:
        return PInt8;
      case 8:
        return PInt16;
      case 9:
        return PInt32;
      case 10:
        return PInt64;
      case 11:
        return PFloat;
      case 12:
        return PDouble;
      case 13:
        return PBool;
      case 14:
        return Bool;
      case 15:
        return ArrayInt8;
      case 16:
        return ArrayInt16;
      case 17:
        return ArrayInt32;
      case 18:
        return ArrayInt64;
      case 19:
        return ArrayFloat;
      case 20:
        return ArrayDouble;
      case 21:
        return ArrayBool;
      case 22:
        return GeoPoint;
      case 23:
        return GeoLineString;
      case 24:
        return Cursor;
      case 25:
        return GeoPolygon;
      case 26:
        return GeoMultiPolygon;
      default:
        return null;
    }
  }
}
