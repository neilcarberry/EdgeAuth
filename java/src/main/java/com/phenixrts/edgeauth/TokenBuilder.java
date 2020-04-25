/**
 * Copyright 2019 Phenix Real Time Solutions, Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phenixrts.edgeauth;

import java.util.Date;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.jetbrains.annotations.Contract;

/**
 * Token builder helper class to create digest tokens that can be used with the Phenix platform.
 */
public final class TokenBuilder {
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_ORIGIN_STREAM_ID = "originStreamId";
  private static final String FIELD_REQUIRED_TAG = "requiredTag";
  private static final String FIELD_APPLY_TAGS = "applyTags";
  private static final String FIELD_CAPABILITIES = "capabilities";
  private String applicationId;
  private String secret;
  private final JsonObjectBuilder tokenBuilder;
  private JsonArrayBuilder capabilitiesBuilder;
  private JsonArrayBuilder tagBuilder;

  /**
   * Token Builder Constructor.
   */
  public TokenBuilder() {
    this.tokenBuilder = Json.createObjectBuilder();
  }

  /**
   * The application ID used to sign the token (required).
   *
   * @param applicationId the application ID to sign the token
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder withApplicationId(String applicationId) {
    if (applicationId == null) {
      throw new RuntimeException("Application ID must not be null");
    }

    this.applicationId = applicationId;

    return this;
  }

  /**
   * The secret used to sign the token (required).
   *
   * @param secret the shared secret to sign the token
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder withSecret(String secret) {
    if (secret == null) {
      throw new RuntimeException("Secret must not be null");
    }

    this.secret = secret;

    return this;
  }

  /**
   * Set a capability for the token, e.g. to publish a stream. (optional)
   *
   * @param capability a valid capability
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder withCapability(String capability) {
    if (capability == null) {
      throw new RuntimeException("Capability must not be null");
    }

    if (capabilitiesBuilder == null) {
      this.capabilitiesBuilder = Json.createArrayBuilder();
    }

    this.capabilitiesBuilder.add(capability);

    return this;
  }

  /**
   * Expires the token in the given time.
   * NOTE: Your time must be synced with the atomic clock for expiration time to work properly.
   *
   * @param seconds the time in seconds
   * @return itself
   */
  @Contract("_ -> this")
  public TokenBuilder expiresInSeconds(long seconds) {
    this.tokenBuilder.add(DigestTokens.FIELD_EXPIRES, new Date().getTime() + (seconds * 1000));

    return this;
  }

  /**
   * Expires the token at the given date.
   * NOTE: Your time must be synced with the atomic clock for expiration time to work properly.
   *
   * @param expirationDate the expiration date
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder expiresAt(Date expirationDate) {
    if (expirationDate == null) {
      throw new RuntimeException("Expiration date must not be null");
    }
    this.tokenBuilder.add(DigestTokens.FIELD_EXPIRES, expirationDate.getTime());

    return this;
  }

  /**
   * Limit the token to authentication only. (optional)
   *
   * @return itself
   */
  @Contract(" -> this")
  public TokenBuilder forAuthenticationOnly() {
    this.tokenBuilder.add(FIELD_TYPE, "auth");

    return this;
  }

  /**
   * Limit the token to streaming only. (optional)
   *
   * @return itself
   */
  @Contract(" -> this")
  public TokenBuilder forStreamingOnly() {
    this.tokenBuilder.add(FIELD_TYPE, "stream");

    return this;
  }

  /**
   * Limit the token to publishing only. (optional)
   *
   * @return itself
   */
  @Contract(" -> this")
  public TokenBuilder forPublishingOnly() {
    this.tokenBuilder.add(FIELD_TYPE, "publish");

    return this;
  }

  /**
   * Limit the token to the specified origin stream ID. (optional)
   *
   * @param originStreamId the origin stream ID
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder forOriginStream(String originStreamId) {
    if (originStreamId == null) {
      throw new RuntimeException("Origin Stream ID must not be null");
    }

    this.tokenBuilder.add(FIELD_ORIGIN_STREAM_ID, originStreamId);

    return this;
  }

  /**
   * Limit the token to the specified channel ID. (optional)
   *
   * @param channelId the channel ID
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder forChannel(String channelId) {
    if (channelId == null) {
      throw new RuntimeException("Channel ID must not be null");
    }

    return this.forTag("channelId:" + channelId);
  }

  /**
   * Limit the token to the specified channel alias. (optional)
   *
   * @param channelAlias the channel alias
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder forChannelAlias(String channelAlias) {
    if (channelAlias == null) {
      throw new RuntimeException("Channel alias must not be null");
    }

    return this.forTag("channelAlias:" + channelAlias);
  }

  /**
   * Limit the token to the specified tag on the origin stream. (optional)
   *
   * @param tag the tag required on the origin stream
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder forTag(String tag) {
    if (tag == null) {
      throw new RuntimeException("Tag must not be null");
    }

    this.tokenBuilder.add(FIELD_REQUIRED_TAG, tag);

    return this;
  }

  /**
   * Apply the tag to the stream when it is setup. (optional)
   *
   * @param tag the tag added to the new stream
   * @return itself
   */
  @Contract("null -> fail, _ -> this")
  public TokenBuilder applyTag(String tag) {
    if (tag == null) {
      throw new RuntimeException("Tag must not be null");
    }

    if (this.tagBuilder == null) {
      this.tagBuilder = Json.createArrayBuilder();
    }

    this.tagBuilder.add(tag);

    return this;
  }

  /**
   * Build the signed token.
   *
   * @return the signed token that can be used with the Phenix platform
   */
  public String build() {
    final DigestTokens digestTokens = new DigestTokens();

    if (this.capabilitiesBuilder != null) {
      this.tokenBuilder.add(FIELD_CAPABILITIES, this.capabilitiesBuilder);
    }

    if (this.tagBuilder != null) {
      this.tokenBuilder.add(FIELD_APPLY_TAGS, this.tagBuilder);
    }

    return digestTokens.signAndEncode(this.applicationId, this.secret, this.tokenBuilder.build());
  }
}