> api文档生成说明

1. 依赖
```xml
<plugin>
    <groupId>com.github.shalousun</groupId>
    <artifactId>smart-doc-maven-plugin</artifactId>
    <version>${smart-doc.version}</version>
</plugin>
```

2. 配置
```json
{
  "serverUrl": "http://localhost:8080",
  "allInOne": true,
  "outPath": "./src/main/resources/static/doc",
  "createDebugPage": true,
  "allInOneDocFileName":"api.html",
  "projectName": "tools",
  "requestHeaders": [{
    "name": "Accept-Language",
    "type": "string",
    "desc": "请求头中的语种",
    "value":"en-US,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
    "required": true
  }]
}
```

3. 生成文档
```bash
mvn -X smart-doc:html -Dfile.encoding=UTF-8  -pl :tools -am
```