/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx

import java.lang.management.ManagementFactory
import javax.management.{ ObjectName, InstanceNotFoundException }
import org.junit.{ Test, After }
import org.junit.Assert._
import com.tzavellas.sse.jmx.export.ObjectNamingStrategies
import com.tzavellas.sse.jmx.export.Managed

class MBeanProxyFactoyTest {

  object factory extends MBeanProxyFactory {
    val server = ManagementFactory.getPlatformMBeanServer
    val namingStrategy = ObjectNamingStrategies.default
  }

  val exporter = new export.MBeanExporter
  val objectName = new ObjectName("com.tzavellas.sse.jmx.test:type=Example")
  
  @After
  def unregisterMBean() {
    exporter.unregisterMBean(objectName, ignore=true)
  }
  
  @Test(expected=classOf[IllegalArgumentException])
  def cannot_request_proxy_without_interface() {
    factory.proxyOf[String]()
  }
  
  @Test(expected=classOf[IllegalArgumentException])
  def cannot_request_proxy_with_interface_not_mbean() {
    factory.proxyOf[Runnable]()
  }
  
  @Test
  def request_an_mbean_proxy() {
    val mbean = new Example1
    exporter.export(mbean, objectName)
    val proxy = factory.proxyOf[Example1MBean](objectName)
    assertEquals(mbean.operation, proxy.operation)
  }
  
  @Test
  def request_an_mxbean_proxy() {
    val mbean = new Example2
    exporter.export(mbean, objectName)
    val proxy = factory.proxyOf[Example2MXBean](objectName)
    assertEquals(mbean.operation, proxy.operation)
  }
  
  @Test
  def test_dynamic_access_to_mbean() {
    val mbean = new AnnotatedObject
    exporter.export(mbean, objectName)
    
    val proxy = factory.dynamicProxyOf(objectName)
    assertEquals(mbean.attr, proxy.attr)
    assertEquals(mbean.attr, proxy.operation())
    //XXX Does not work because of a compiler bug that will get fixed in 2.10.1
    //proxy.attr = 1
    //assertEquals(1, mbean.attr)
  }


  // -- test classes ----------------------------------------------------------
  
  class AnnotatedObject {
    @Managed var attr = 10
    @Managed def operation = attr
  }
  
  trait Example1MBean { def operation: Int }
  class Example1 extends Example1MBean { def operation = 1 }
  
  trait Example2MXBean { def operation: Int }
  class Example2 extends Example2MXBean { def operation = 2 }
}