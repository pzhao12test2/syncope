/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.client.console.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.console.commons.Constants;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Toggle panel.
 *
 * @param <T>
 */
public abstract class TogglePanel<T extends Serializable> extends Panel {

    private static final long serialVersionUID = -2025535531121434056L;

    protected static final Logger LOG = LoggerFactory.getLogger(TogglePanel.class);

    private enum Status {
        INACTIVE,
        ACTIVE

    }

    private final WebMarkupContainer container;

    private Status status = Status.INACTIVE;

    private final Label header;

    private List<Component> outherObjects = new ArrayList<>();

    public TogglePanel(final String id) {
        super(id);
        setRenderBodyOnly(true);
        setOutputMarkupId(true);

        container = new WebMarkupContainer("togglePanelContainer");
        add(container.setMarkupId(id));

        header = new Label("label", StringUtils.EMPTY);
        header.setOutputMarkupId(true);
        container.add(header);

        container.add(new AjaxLink<Void>("close") {

            private static final long serialVersionUID = 5538299138211283825L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                toggle(target, false);
            }

        }.add(new AjaxEventBehavior(Constants.ON_CLICK) {

            private static final long serialVersionUID = -9027652037484739586L;

            @Override
            protected String findIndicatorId() {
                return StringUtils.EMPTY;
            }

            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                // do nothing
            }
        }));

        add(new ListView<Component>("outherObjectsRepeater", outherObjects) {

            private static final long serialVersionUID = -9180479401817023838L;

            @Override
            protected void populateItem(final ListItem<Component> item) {
                item.add(item.getModelObject());
            }

        });

    }

    /**
     * Add object outside the main container.
     * Use this method just to be not influenced by specific inner object css'.
     * Be sure to provide <tt>outher</tt> as id.
     *
     * @param childs components to be added.
     * @return the current panel instance.
     */
    public TogglePanel<T> addOutherObject(final Component... childs) {
        outherObjects.addAll(Arrays.asList(childs));
        return this;
    }

    /**
     * Add object inside the main container.
     *
     * @param childs components to be added.
     * @return the current panel instance.
     */
    public TogglePanel<T> addInnerObject(final Component... childs) {
        container.addOrReplace(childs);
        return this;
    }

    protected void setHeader(final AjaxRequestTarget target, final String header) {
        this.header.setDefaultModelObject(header.length() >= 40 ? (header.substring(0, 30) + " ... ") : header);
        target.add(this.header);
    }

    /**
     * Force toggle via java. To be used when the onclick has been intercepted before.
     *
     * @param target ajax request target.
     * @param toggle toggle action.
     */
    public void toggle(final AjaxRequestTarget target, final boolean toggle) {
        final String selector = String.format("$(\"div#%s\")", getId());
        if (toggle) {
            if (status == Status.INACTIVE) {
                target.appendJavaScript(
                        selector + ".toggle(\"slow\");"
                        + selector + ".attr(\"class\", \"topology-menu active-topology-menu\");");
                status = Status.ACTIVE;
            }
        } else if (status == Status.ACTIVE) {
            target.appendJavaScript(
                    selector + ".toggle(\"slow\");"
                    + selector + ".attr(\"class\", \"topology-menu inactive-topology-menu\");");
            status = Status.INACTIVE;
        }
    }
}