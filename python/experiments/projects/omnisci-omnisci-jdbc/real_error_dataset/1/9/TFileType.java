/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.mapd.thrift.server;


public enum TFileType implements org.apache.thrift.TEnum {
  DELIMITED(0),
  POLYGON(1),
  PARQUET(2);

  private final int value;

  private TFileType(int value) {
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
  public static TFileType findByValue(int value) { 
    switch (value) {
      case 0:
        return DELIMITED;
      case 1:
        return POLYGON;
      case 2:
        return PARQUET;
      default:
        return null;
    }
  }
}
