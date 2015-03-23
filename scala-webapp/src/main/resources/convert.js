function convert(json) {
  var jObj = JSON.parse(json);
  var XYZ = Java.type('XYZ');
  var xyz = new XYZ;

  xyz.x = jObj.x;
  xyz.y = jObj.y;
  xyz.z = jObj.z;
  print(xyz);
  return xyz;
}