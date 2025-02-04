/*
 * Copyright (c) 2002-2021 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.loboevolution.test.css;

import org.junit.Test;
import org.loboevolution.test.driver.LoboUnitTest;

/**
 * Tests for {@link org.loboevolution.html.js.css.MediaQueryListImpl}.
 */

public class MediaQueryListTest extends LoboUnitTest {

    @Test
    public void matches() {
        final String html
                = "<html><head><script>\n"
                + "  function test() {\n"
                + "    if (window.matchMedia) {\n"
                + "      alert(window.matchMedia('(min-width: 400px)').matches);\n"
                + "    }\n"
                + "  }\n"
                + "</script></head><body onload='test()'>\n"
                + "</body></html>";
        final String[] messages = {"true"};
        checkHtmlAlert(html, messages);
    }


    @Test
    public void listener() {
        final String html
                = "<html><head><script>\n"
                + "  function listener(mql) {\n"
                + "    alert(mql);\n"
                + "  }\n"
                + "  function test() {\n"
                + "    if (window.matchMedia) {\n"
                + "      var mql = window.matchMedia('(min-width: 400px)');\n"
                + "      mql.addListener(listener);\n"
                + "      alert('added');\n"
                + "      mql.removeListener(listener);\n"
                + "      alert('removed');\n"
                + "    }\n"
                + "  }\n"
                + "</script></head><body onload='test()'>\n"
                + "</body></html>";
        final String[] messages = {"added", "removed"};
        checkHtmlAlert(html, messages);
    }
}
