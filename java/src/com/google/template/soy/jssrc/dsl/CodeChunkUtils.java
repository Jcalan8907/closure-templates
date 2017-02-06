/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.jssrc.dsl;

import static com.google.template.soy.jssrc.dsl.CodeChunk.WithValue.LITERAL_EMPTY_STRING;
import static com.google.template.soy.jssrc.dsl.CodeChunk.id;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.jssrc.dsl.CodeChunk.RequiresCollector;
import com.google.template.soy.jssrc.restricted.JsExprUtils;
import java.util.List;

/** Utility methods for working with CodeChunks. */
public final class CodeChunkUtils {

  /** Useful for code generation, but not so useful as to belong in {@link CodeChunk}. */
  public static final CodeChunk.WithValue OPT_DATA = id("opt_data");

  private CodeChunkUtils() {}

  /**
   * Builds a {@link CodeChunk.WithValue} that represents the concatenation of the given code
   * chunks. The {@code +} operator is used for concatenation.
   *
   * <p>The resulting chunk is not guaranteed to be string-valued if the first two operands do not
   * produce strings when combined with the plus operator; e.g. 2+2 might be 4 instead of '22'.
   *
   * <p>This is a port of {@link JsExprUtils#concatJsExprs}, which should eventually go away.
   * TODO(user): make that go away.
   */
  public static CodeChunk.WithValue concatChunks(List<? extends CodeChunk.WithValue> chunks) {

    if (chunks.isEmpty()) {
      return LITERAL_EMPTY_STRING;
    }

    CodeChunk.WithValue accum = chunks.get(0);
    for (CodeChunk.WithValue chunk : chunks.subList(1, chunks.size())) {
      accum = accum.plus(chunk);
    }
    return accum;
  }

  /**
   * Builds a {@link CodeChunk.WithValue} that represents the concatenation of the given code
   * chunks. This doesn't assume the values represented by the inputs are necessarily strings, but
   * guarantees that the value represented by the output is a string.
   *
   * <p>This is a port of {@link JsExprUtils#concatJsExprsForceString}, which should eventually go
   * away. TODO(user): make that go away.
   */
  public static CodeChunk.WithValue concatChunksForceString(
      List<? extends CodeChunk.WithValue> chunks) {
    if (!chunks.isEmpty()
        && chunks.get(0).isRepresentableAsSingleExpression()
        && JsExprUtils.isStringLiteral(
            chunks.get(0).assertExprAndCollectRequires(RequiresCollector.NULL))) {
      return concatChunks(chunks);
    } else if (chunks.size() > 1
        && chunks.get(1).isRepresentableAsSingleExpression()
        && JsExprUtils.isStringLiteral(
            chunks.get(1).assertExprAndCollectRequires(RequiresCollector.NULL))) {
      return concatChunks(chunks);
    } else {
      return concatChunks(
          ImmutableList.<CodeChunk.WithValue>builder()
              .add(LITERAL_EMPTY_STRING)
              .addAll(chunks)
              .build());
    }
  }
}