/*
 * Copyright 2015 Google Inc.
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

package com.google.template.soy;

import com.google.common.base.Optional;
import com.google.common.io.ByteSink;
import com.google.common.io.Files;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;

/** Executable for compiling a set of Soy files into corresponding Java class files in a jar. */
public final class SoyToJbcSrcCompiler extends AbstractSoyCompiler {
  @Option(
    name = "--output",
    required = true,
    usage =
        "[Required] The file name of the JAR file to be written.  Each compiler"
            + " invocation will produce exactly one file"
  )
  private File output;

  @Option(
    name = "--outputSrcJar",
    required = false,
    usage =
        "[Optional] The file name of the JAR containing sources to be written.  Each compiler"
            + " invocation will produce exactly one such file.  This may be useful for enabling"
            + "IDE debugging scenarios"
  )
  private File outputSrcJar;

  private SoyToJbcSrcCompiler() {}

  @Override
  boolean acceptsSourcesAsArguments() {
    return false;
  }

  @Override
  void compile(SoyFileSet.Builder sfsBuilder) throws IOException {
    // Disallow external call entirely in JbcSrc.  JbcSrc needs callee information to generate
    // correct escaping code.
    sfsBuilder.setAllowExternalCalls(false);
    SoyFileSet sfs = sfsBuilder.build();
    Optional<ByteSink> srcJarSink = Optional.absent();
    if (outputSrcJar != null) {
      srcJarSink = Optional.of(Files.asByteSink(outputSrcJar));
    }
    sfs.compileToJar(Files.asByteSink(output), srcJarSink);
  }

  public static void main(final String[] args) {
    new SoyToJbcSrcCompiler().run(args);
  }
}
