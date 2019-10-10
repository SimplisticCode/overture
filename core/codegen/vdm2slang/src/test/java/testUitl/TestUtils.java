/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package testUitl;

import junit.framework.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2slang.SlangGen;
import org.overture.typechecker.util.TypeCheckerUtil;

import java.io.File;
import java.util.List;

public class TestUtils
{

    public void RunTest(String inputFileName, String ExpectedFileOutput) throws Exception {
        File inputFile = new File("src/test/resources/" + inputFileName);
        File resultFile = new File("src/test/resources/ResultFiles/" + ExpectedFileOutput);

        String slangCode = generateModules(inputFile);

        String fileContent = GeneralUtils.readFromFile(resultFile);
        validateCode(fileContent, slangCode);
    }


    private void validateCode(String expectedCode, String actualCode)
    {
        Assert.assertEquals("Got unexpected code", expectedCode, actualCode);
    }

    private String generateModules(File file)
            throws AnalysisException
    {
        TypeCheckerUtil.TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(file);

        Assert.assertTrue("Expected no parse errors", tcResult.parserResult.errors.isEmpty());
        Assert.assertTrue("Expected no type errors", tcResult.errors.isEmpty());

        SlangGen codeGen = new SlangGen();

        List<INode> nodes = CodeGenBase.getNodes(tcResult.result);
        List<GeneratedModule> data = codeGen.generate(nodes).getClasses();

        assertSingleClass(data);

        return data.get(0).getContent();
    }

    private void assertSingleClass(List<GeneratedModule> classes)
    {
        Assert.assertEquals("Expected one class to be generated", 1, classes.size());
    }

}
