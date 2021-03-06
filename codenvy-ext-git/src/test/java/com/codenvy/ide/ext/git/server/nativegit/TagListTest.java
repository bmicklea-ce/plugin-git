/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.Tag;
import com.codenvy.ide.ext.git.shared.TagListRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Eugene Voevodin
 */
public class TagListTest extends BaseTest {

    @BeforeMethod
    protected void createTags() throws Exception {
        NativeGit defaultGit = new NativeGit(getRepository().toFile());
        defaultGit.createTagCreateCommand().setName("first-tag").execute();
        defaultGit.createTagCreateCommand().setName("first-tag-other").execute();
        defaultGit.createTagCreateCommand().setName("second-tag").execute();
    }

    @Test
    public void testTagList() throws GitException {
        validateTags(getConnection().tagList(
                newDTO(TagListRequest.class)), "first-tag", "first-tag-other", "second-tag");
    }

    @Test
    public void testTagListPattern() throws GitException {
        TagListRequest request = newDTO(TagListRequest.class);
        request.setPattern("first*");
        validateTags(getConnection().tagList(request), "first-tag", "first-tag-other");
    }

    protected void validateTags(List<Tag> tagList, String... expNames) {
        assertEquals(tagList.size(), expNames.length);
        List<String> names = new ArrayList<>(tagList.size());
        for (Tag t : tagList)
            names.add(t.getName());
        for (String name : expNames)
            assertTrue(names.contains(name), "Expected tag " + name + " not found in result. ");
    }
}
