package com.york.cs.server.db;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.couchbase.client.CouchbaseClient;
import com.york.cs.server.controllers.AuthenticationController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionManager implements ServletContextListener {

  private static CouchbaseClient client;

  
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    logger.info( "Connecting to Couchbase Cluster");
    ArrayList<URI> nodes = new ArrayList<URI>();
    nodes.add(URI.create("http://127.0.0.1:8091/pools"));
    /*try {
      client = new CouchbaseClient(nodes, "Users", "");
    } catch (IOException ex) {
      logger.equals( ex.getMessage());
    }*/
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    logger.info("Disconnecting from Couchbase Cluster");
    client.shutdown(60, TimeUnit.SECONDS);
  }

  public static CouchbaseClient getInstance() {
    return client;
  }

}