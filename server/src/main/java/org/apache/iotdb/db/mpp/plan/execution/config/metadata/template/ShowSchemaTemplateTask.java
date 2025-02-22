/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.mpp.plan.execution.config.metadata.template;

import org.apache.iotdb.db.metadata.template.Template;
import org.apache.iotdb.db.mpp.common.header.DatasetHeader;
import org.apache.iotdb.db.mpp.common.header.HeaderConstant;
import org.apache.iotdb.db.mpp.plan.execution.config.ConfigTaskResult;
import org.apache.iotdb.db.mpp.plan.execution.config.IConfigTask;
import org.apache.iotdb.db.mpp.plan.execution.config.executor.IConfigTaskExecutor;
import org.apache.iotdb.db.mpp.plan.statement.metadata.template.ShowSchemaTemplateStatement;
import org.apache.iotdb.rpc.TSStatusCode;
import org.apache.iotdb.tsfile.read.common.block.TsBlockBuilder;
import org.apache.iotdb.tsfile.utils.Binary;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShowSchemaTemplateTask implements IConfigTask {

  private final ShowSchemaTemplateStatement showSchemaTemplateStatement;

  public ShowSchemaTemplateTask(ShowSchemaTemplateStatement showSchemaTemplateStatement) {
    this.showSchemaTemplateStatement = showSchemaTemplateStatement;
  }

  @Override
  public ListenableFuture<ConfigTaskResult> execute(IConfigTaskExecutor configTaskExecutor)
      throws InterruptedException {
    return configTaskExecutor.showSchemaTemplate(this.showSchemaTemplateStatement);
  }

  public static void buildTSBlock(
      List<Template> templateList, SettableFuture<ConfigTaskResult> future) {
    TsBlockBuilder builder =
        new TsBlockBuilder(HeaderConstant.showSchemaTemplate.getRespDataTypes());
    Optional<List<Template>> optional = Optional.ofNullable(templateList);
    optional.orElse(new ArrayList<>()).stream()
        .forEach(
            template -> {
              builder.getTimeColumnBuilder().writeLong(0L);
              builder.getColumnBuilder(0).writeBinary(new Binary(template.getName()));
              builder.declarePosition();
            });
    DatasetHeader datasetHeader = HeaderConstant.showSchemaTemplate;
    future.set(new ConfigTaskResult(TSStatusCode.SUCCESS_STATUS, builder.build(), datasetHeader));
  }
}
