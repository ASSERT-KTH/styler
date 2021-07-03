/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.omnisci.thrift.server;


public enum TExecuteMode implements org.apache.thrift.TEnum {
  GPU(1),
  CPU(2);

  private final int value;

  private TExecuteMode(int value) {
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
  @org.apache.thrift.annotation.Nullable
  public static TExecuteMode findByValue(int value) { 
    switch (value) {
      case 1:
        return GPU;
      case 2:
        return CPU;
      default:
        return null;
    }
  }
}
