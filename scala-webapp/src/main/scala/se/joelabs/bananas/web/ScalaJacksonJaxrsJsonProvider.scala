package se.joelabs.bananas.web

import java.io.{InputStream, OutputStream}
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import javax.ws.rs.core.{MediaType, MultivaluedMap}
import javax.ws.rs.ext.Provider
import javax.ws.rs.{Consumes, Produces}

import com.fasterxml.jackson.core.{JsonEncoding, JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey
import com.fasterxml.jackson.jaxrs.json.{JacksonJaxbJsonProvider, JsonEndpointConfig}
import com.fasterxml.jackson.jaxrs.util.ClassKey
import com.fasterxml.jackson.module.scala.DefaultScalaModule

@Provider
@Consumes(Array("application/*+json", "text/json", "application/json"))
@Produces(Array("application/*+json", "text/json", "application/json"))
class ScalaJacksonJaxrsJsonProvider extends JacksonJaxbJsonProvider {

  override def isReadable(`type`: Class[_], genericType: Type, annotations: Array[Annotation], mediaType: MediaType): Boolean =
    super.isReadable(`type`, genericType, annotations, mediaType)

  override def isWriteable(`type`: Class[_], genericType: Type, annotations: Array[Annotation], mediaType: MediaType): Boolean =
    super.isWriteable(`type`, genericType, annotations, mediaType)

  protected val readers = new ConcurrentHashMap[ClassAnnotationKey, JsonEndpointConfig]

  override def readFrom(typ: Class[Object],
                        genericType: Type,
                        annotations: Array[Annotation],
                        mediaType: MediaType,
                        httpHeaders: MultivaluedMap[String, String],
                        entityStream: InputStream): Object = {
    val key: ClassAnnotationKey = new ClassAnnotationKey(typ, annotations)
    var endpoint: JsonEndpointConfig = readers.get(key)
    // not yet resolved (or not cached any more)? Resolve!
    if (endpoint == null) {
      val mapper: ObjectMapper = locateMapper(typ, mediaType)
      mapper.registerModule(DefaultScalaModule)
      endpoint = _configForReading(mapper, annotations)
      readers.put(key, endpoint)
    }
    val reader: ObjectReader = endpoint.getReader
    val jp: JsonParser = _createParser(reader, entityStream)
    // If null is returned, considered to be empty stream
    if (jp == null || jp.nextToken() == null) {
      return null
    }
    // [Issue#1]: allow 'binding' to JsonParser
    if (typ.asInstanceOf[Class[_]] == classOf[JsonParser]) {
      return jp
    }

    reader.withType(genericType).readValue(jp)
  }

  protected val writers = new ConcurrentHashMap[ClassAnnotationKey, JsonEndpointConfig]

  override def writeTo(value: Object,
                       typ: Class[_],
                       genericType: Type,
                       annotations: Array[Annotation],
                       mediaType: MediaType,
                       httpHeaders: MultivaluedMap[String, AnyRef],
                       entityStream: OutputStream): Unit = {
    val key = new ClassAnnotationKey(typ, annotations)
    var endpoint: JsonEndpointConfig = writers.get(key)

    // not yet resolved (or not cached any more)? Resolve!
    if (endpoint == null) {
      val mapper: ObjectMapper = locateMapper(typ, mediaType)
      mapper.registerModule(DefaultScalaModule)

      endpoint = _configForWriting(mapper, annotations)

      // and cache for future reuse
      writers.put(key, endpoint)
    }

    var writer: ObjectWriter = endpoint.getWriter
    var withIndentOutput = false; // no way to replace _serializationConfig

    /* 27-Feb-2009, tatu: Where can we find desired encoding? Within
    *   HTTP headers?
    */
    val enc: JsonEncoding = findEncoding(mediaType, httpHeaders)
    val jg: JsonGenerator = writer.getFactory.createGenerator(entityStream, enc)

    try {
      // Want indentation?
      if (writer.isEnabled(SerializationFeature.INDENT_OUTPUT) || withIndentOutput) {
        jg.useDefaultPrettyPrinter()
      }
      // 04-Mar-2010, tatu: How about type we were given? (if any)
      var rootType: JavaType = null

      if (genericType != null && value != null) {
        /* 10-Jan-2011, tatu: as per [JACKSON-456], it's not safe to just force root
        *    type since it prevents polymorphic type serialization. Since we really
        *    just need this for generics, let's only use generic type if it's truly
        *    generic.
        */
        if (genericType.getClass != classOf[Class[_]]) {
          // generic types are other impls of 'java.lang.reflect.Type'
          /* This is still not exactly right; should root type be further
          * specialized with 'value.getClass()'? Let's see how well this works before
          * trying to come up with more complete solution.
          */
          rootType = writer.getTypeFactory.constructType(genericType)
          /* 26-Feb-2011, tatu: To help with [JACKSON-518], we better recognize cases where
          *    type degenerates back into "Object.class" (as is the case with plain TypeVariable,
          *    for example), and not use that.
          */
          if (rootType.getRawClass == classOf[Object]) {
            rootType = null
          }
        }
      }

      // Most of the configuration now handled through EndpointConfig, ObjectWriter
      // but we may need to force root type:
      if (rootType != null) {
        writer = writer.withType(rootType)
      }
      writer.writeValue(jg, endpoint.modifyBeforeWrite(value));
    } finally {
      jg.close()
    }
  }

  protected class ClassAnnotationKey(clazz: Class[_], pAnnotations: Array[Annotation]) {
    private var annotations: AnnotationBundleKey = new AnnotationBundleKey(pAnnotations)
    private var classKey: ClassKey = new ClassKey(clazz)
    private var hash: Int = 31 * this.annotations.hashCode() + classKey.hashCode()

    def canEqual(other: Any): Boolean = other.isInstanceOf[ClassAnnotationKey]

    override def equals(o: Any): Boolean = {
      o match {
        case that: ClassAnnotationKey =>
          if (that eq this) return true
          else {
            if (!annotations.equals(that.annotations)) return false
            if (!classKey.equals(that.classKey)) return false
          }
        case _ =>
          if (o == null || getClass != o.getClass) return false
      }

      true
    }

    override def hashCode(): Int = hash
  }

};

