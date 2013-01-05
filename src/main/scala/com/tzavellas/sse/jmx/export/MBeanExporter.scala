/* ------------------- sse-jmx ------------------- *\
 * Licensed under the Apache License, Version 2.0. *
 * Author: Spiros Tzavellas                        *
\* ----------------------------------------------- */
package com.tzavellas.sse.jmx.export

import java.lang.management.ManagementFactory
import javax.management.{ObjectName, MBeanServer}
import javax.management.modelmbean.RequiredModelMBean
import com.tzavellas.sse.jmx.{MBeanRegistrationSupport, IfAlreadyExists} 

/**
 * Exports objects to JMX.
 *
 * @param server         the MBeanServet to use of registering the objects
 * @param namingStrategy consulted during the creation of the ObjectName 
 * @param assembler      used to create MBean models for classes that are not MBeans
 * @param ifAlredyExists what to do when a MBean with the same name is already registered
 */
final class MBeanExporter (
  val assembler: MBeanInfoAssembler = AnnotationMBeanInfoAssembler,
  val namingStrategy: PartialFunction[Class[_], ObjectName] = JmxNaming.default,
  val ifAlreadyExists: IfAlreadyExists.Enum = IfAlreadyExists.Fail,
  val server: MBeanServer = ManagementFactory.getPlatformMBeanServer)
    extends MBeanRegistrationSupport {
  
  
  /**
   * Same as {@link #export(ref, name)} except that the ObjectName is defined
   * from the {@link #objectName(clazz)} method.
   * 
   * @param ref the object to register
   */
  def export(ref: AnyRef) {
    export(ref, objectName(ref.getClass))
  }
  
  /**
   * Export the specified object to JMX.
   * 
   * <p>If the specified object is already an MBean (standard MBean of MXBean)
   * then the specified object get registered. If the object is not an MBean then
   * the configured {@code MBeanInfoAssembler} is used to construct a
   * {@code ModelMBeanInfo} and the object gets registered as a {@code ModelMBean}.
   * </p> 
   * 
   * @param ref the object to register
   * @param name the ObjectName to use for registering the object
   */
  def export(ref: AnyRef, name: ObjectName) {
    def modelMBean = {
      val info = new NoGetterAndSetterMBeanInfo(assembler.createMBeanInfo(ref.getClass))
      val model = new RequiredModelMBean(info)
      model.setManagedResource(ref, "ObjectReference")
      model
    }
    
    val mbean = if (JmxUtils.isMBean(ref.getClass)) ref else modelMBean
    registerMBean(mbean, name, ifAlreadyExists)
  }
  
  /**
   * Create an {@code ObjectName} for the specified class.
   * 
   * <p>This method will first check if the specified class is annotated with
   * the {@code ManagedResource} annotation and use the name found in the
   * annotation. If the specified class is not annotated then it will use the
   * {@code ObjectName} suggested by the configured {@code namingStrategy}.
   * 
   * @param clazz the class to use for deriving the ObjectName
   */
  def objectName(clazz: Class[_]): ObjectName = namingStrategy(clazz)
}