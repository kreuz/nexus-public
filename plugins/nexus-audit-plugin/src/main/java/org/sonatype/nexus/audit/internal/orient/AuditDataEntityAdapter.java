/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.audit.internal.orient;

import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.audit.AuditData;
import org.sonatype.nexus.orient.OClassNameBuilder;
import org.sonatype.nexus.orient.entity.FieldCopier;
import org.sonatype.nexus.orient.entity.IterableEntityAdapter;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ODefaultClusterSelectionStrategy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link AuditData} entity adapter.
 *
 * @since 3.1
 */
@Named
@Singleton
public class AuditDataEntityAdapter
    extends IterableEntityAdapter<AuditData>
{
  private static final String DB_CLASS = new OClassNameBuilder()
      .type("audit_data")
      .build();

  private static final String P_DOMAIN = "domain";

  private static final String P_TYPE = "type";

  private static final String P_CONTEXT = "context";

  private static final String P_TIMESTAMP = "timestamp";

  private static final String P_NODE_ID = "nodeId";

  private static final String P_INITIATOR = "initiator";

  private static final String P_ATTRIBUTES = "attributes";

  public AuditDataEntityAdapter() {
    super(DB_CLASS);
  }

  @Override
  protected void defineType(final OClass type) {
    type.createProperty(P_DOMAIN, OType.STRING)
        .setNotNull(true);
    type.createProperty(P_TYPE, OType.STRING)
        .setNotNull(true);
    type.createProperty(P_CONTEXT, OType.STRING)
        .setNotNull(true);
    type.createProperty(P_TIMESTAMP, OType.LONG)
        .setNotNull(true);
    type.createProperty(P_NODE_ID, OType.STRING)
        .setNotNull(true);
    type.createProperty(P_INITIATOR, OType.STRING)
        .setNotNull(true);
    type.createProperty(P_ATTRIBUTES, OType.EMBEDDEDMAP);

    // ensure we have the default strategy, so that only the default cluster-id will be used for new documents
    type.setClusterSelection(ODefaultClusterSelectionStrategy.NAME);
  }

  @Override
  protected AuditData newEntity() {
    return new AuditData();
  }

  @Override
  protected void readFields(final ODocument document, final AuditData entity) throws Exception {
    entity.setDomain(document.field(P_DOMAIN, OType.STRING));
    entity.setType(document.field(P_TYPE, OType.STRING));
    entity.setContext(document.field(P_CONTEXT, OType.STRING));
    entity.setTimestamp(document.field(P_TIMESTAMP, OType.DATETIME));
    entity.setNodeId(document.field(P_NODE_ID, OType.STRING));
    entity.setInitiator(document.field(P_INITIATOR, OType.STRING));

    // deeply copy attributes to divorce from document
    Map<String, String> attributes = document.field(P_ATTRIBUTES, OType.EMBEDDEDMAP);
    attributes = FieldCopier.copyIf(attributes);
    if (attributes != null) {
      entity.getAttributes().putAll(attributes);
    }
  }

  @Override
  protected void writeFields(final ODocument document, final AuditData entity) throws Exception {
    document.field(P_DOMAIN, entity.getDomain());
    document.field(P_TYPE, entity.getType());
    document.field(P_CONTEXT, entity.getContext());
    document.field(P_TIMESTAMP, entity.getTimestamp());
    document.field(P_NODE_ID, entity.getNodeId());
    document.field(P_INITIATOR, entity.getInitiator());
    document.field(P_ATTRIBUTES, entity.getAttributes());
  }

  private static final String BROWSE_SKIP_LIMIT_QUERY = String.format("SELECT FROM %s SKIP ? LIMIT ?", DB_CLASS);

  private static final String BROWSE_SKIP_QUERY = String.format("SELECT FROM %s SKIP ?", DB_CLASS);

  @Nullable
  public Iterable<ODocument> browseDocuments(final ODatabaseDocumentTx db, final long offset, @Nullable Long limit) {
    log.trace("Browse; offset: {}, limit: {}", offset, limit);

    OResultSet<ODocument> results;
    if (limit == null) {
      results = db.query(new OSQLSynchQuery<ODocument>(BROWSE_SKIP_QUERY), offset);
    }
    else {
      results = db.query(new OSQLSynchQuery<ODocument>(BROWSE_SKIP_LIMIT_QUERY), offset, limit);
    }

    if (results.isEmpty()) {
      log.trace("No results");
      return null;
    }
    return results;
  }
}