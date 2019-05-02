/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2019
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package org.github._1c_syntax.bsl.languageserver.diagnostics;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.eclipse.lsp4j.DiagnosticRelatedInformation;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticMetadata;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticScope;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticSeverity;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticType;
import org.github._1c_syntax.bsl.languageserver.utils.DiagnosticHelper;
import org.github._1c_syntax.bsl.languageserver.utils.RangeHelper;
import org.github._1c_syntax.bsl.parser.BSLParser;
import org.github._1c_syntax.bsl.parser.BSLParser.NewExpressionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leon Chagelishvili <lChagelishvily@gmail.com>
 */
@DiagnosticMetadata(
  type = DiagnosticType.CODE_SMELL,
  severity = DiagnosticSeverity.MINOR,
  minutesToFix = 10,
  scope = DiagnosticScope.ALL
)


public class NestedConstructorsInStructureDeclarationDiagnostic extends AbstractVisitorDiagnostic{

  private Collection<ParseTree> nestedNewContext = new ArrayList<>();
  private final String relatedMessage = getResourceString("nestedConstructorRelatedMessage");

  @Override
  public ParseTree visitNewExpression(NewExpressionContext ctx) {

    nestedNewContext.clear();

    // Checking that new context is structure declaration with parameters
    BSLParser.TypeNameContext typeName = ctx.typeName();
    if (typeName == null ) {
      return super.visitNewExpression(ctx);
    }

    if(!(DiagnosticHelper.isStructureType(ctx.typeName()) || DiagnosticHelper.isFixedStructureType(ctx.typeName()))){
      return super.visitNewExpression(ctx);
    }

    BSLParser.DoCallContext structureDoCallContext = ctx.doCall();
    if(structureDoCallContext == null) {
      return super.visitNewExpression(ctx);
    }

    // receiving all parameters in structure declaration
    List<BSLParser.CallParamContext> params = structureDoCallContext.callParamList().callParam();
    for (BSLParser.CallParamContext parameter : params) {

      // looking for first nested constructor in parameter
      Collection<ParseTree> nestedNewExpressions = Trees.findAllRuleNodes(parameter, BSLParser.RULE_newExpression);
      nestedNewExpressions.stream()
        .limit(1)
        .filter(parseTree ->{
            BSLParser.DoCallContext doCallContext = ((BSLParser.NewExpressionContext) parseTree).doCall();
            return doCallContext!= null && doCallContext.callParamList().callParam().size() > 0;
        }
        ).collect(Collectors.toCollection(()->nestedNewContext));
    }

    if (nestedNewContext.isEmpty()){
      return super.visitNewExpression(ctx);
    }

    List<DiagnosticRelatedInformation> relatedInformation = new ArrayList<>();

    relatedInformation.add(this.createRelatedInformation(
      RangeHelper.newRange(ctx),
      relatedMessage
    ));

    nestedNewContext.stream()
      .map(expressionContext ->
        this.createRelatedInformation(
          RangeHelper.newRange((BSLParser.NewExpressionContext)expressionContext),
          relatedMessage
        )
      )
      .collect(Collectors.toCollection(() -> relatedInformation));

    addDiagnostic(ctx, relatedInformation);

    return super.visitNewExpression(ctx);
  }

}
