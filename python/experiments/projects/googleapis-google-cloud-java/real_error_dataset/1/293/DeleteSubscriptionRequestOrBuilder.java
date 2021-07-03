// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/pubsub/v1/pubsub.proto

package com.google.pubsub.v1;

public interface DeleteSubscriptionRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.pubsub.v1.DeleteSubscriptionRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The subscription to delete.
   * Format is `projects/{project}/subscriptions/{sub}`.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   */
  java.lang.String getSubscription();
  /**
   * <pre>
   * The subscription to delete.
   * Format is `projects/{project}/subscriptions/{sub}`.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   */
  com.google.protobuf.ByteString
      getSubscriptionBytes();
}
