/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.mapd.thrift.server;


public enum TGeoFileLayerContents implements org.apache.thrift.TEnum {
  EMPTY(0),
  GEO(1),
  NON_GEO(2),
  UNSUPPORTED_GEO(3);

  private final int value;

  private TGeoFileLayerContents(int value) {
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
  public static TGeoFileLayerContents findByValue(int value) { 
    switch (value) {
      case 0:
        return EMPTY;
      case 1:
        return GEO;
      case 2:
        return NON_GEO;
      case 3:
        return UNSUPPORTED_GEO;
      default:
        return null;
    }
  }
}
