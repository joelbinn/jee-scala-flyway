import java.io.InputStream

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import scala.collection.JavaConversions._

object TestStuff extends App {
  val l = List("aa", "aba", "bcde", "kjke", "kk")

  val init = Map[Int, List[String]]()
  val collect = (res: Map[Int, List[String]], e: String) => {
    val len = e.length
    val list: List[String] = res.get(len).map(v => e :: v).getOrElse(List(e))
    val ret: Map[Int, List[String]] = res + (len -> list)
    ret
  }

  private val collected: Map[Int, List[String]] = l.foldLeft(init)(collect)
  println(s"map: ${collected.toString}")

  val col2 = collected
    .filter(entry => entry._1 == 2)
    .foldLeft("")((tot, e) => tot + e._2.foldLeft("")((t2, s2) => t2 + s2))
    .reverse
  println(s"col2=$col2")

  val jsonStream: InputStream = this.getClass.getResourceAsStream("xyz.json")
  var json = scala.io.Source.fromInputStream(jsonStream).getLines().mkString("\n")
  val mapper = new ObjectMapper
  val json_v1 = mapper.writerWithType(classOf[XYZ])
    .writeValueAsString(new XYZ(42, List(new ZYX(99, "baba")), "bule"))
  val json_v2 = mapper.writerWithType(classOf[XYZ_v2])
    .writeValueAsString(new XYZ_v2(33, List(new ZYX(44, "kaka")), "banan", 999L))
  println(s"jsonv1=$json_v1")
  println(s"jsonv2=$json_v2")
  val tree = mapper.reader().readTree(json_v1)
  val xyz1: XYZ_v2 = extract(mapper.reader().readTree(json_v1))
  val xyz2: XYZ_v2 = extract(mapper.reader().readTree(json_v2))

  println(s"tree=$tree")
  println(s"xyz1=$xyz1")
  println(s"xyz2=$xyz2")

  def extract(tree:JsonNode): XYZ_v2 = {
    val version: Int = tree.get("version").asInt()
    version match {
      case 1 =>
        val xyz_v1: XYZ = mapper.reader(classOf[XYZ]).readValue(tree)
        val xyz_v2 = new XYZ_v2
        xyz_v2.x = xyz_v1.x
        xyz_v2.y = xyz_v1.y
        xyz_v2.z = xyz_v1.z
        xyz_v2
      case 2 =>
        mapper.reader(classOf[XYZ_v2]).readValue(tree)
    }
  }
}
