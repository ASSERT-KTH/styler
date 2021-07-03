/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.angel.webapp.page;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

@InterfaceAudience.LimitedPrivate({"YARN", "MapReduce"})
public class FooterBlock extends HtmlBlock {

  @Override
  protected void render(Block html) {
    /*
     * String str = ""; str="user_dir: \n  "+System.getProperty("user.dir"); html.
     * div("#user_dir:"). _(str)._();
     */
  }
}
