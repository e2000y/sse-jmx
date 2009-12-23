package com.tzavellas.sse.jmx.export

import javax.management.modelmbean._

trait MBeanInfoAssembler {
  
  this: MBeanModelExtractor =>
  
  def createMBeanInfo(clazz: Class[_]): ModelMBeanInfo = {
    val attrs = attributes(clazz)
    new ModelMBeanInfoSupport(clazz.getName,
                              description(clazz),
                              attrs,
                              constructors(clazz),
                              operations(clazz, attrs),
                              notifications(clazz))
  }
}
