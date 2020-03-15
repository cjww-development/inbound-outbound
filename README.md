[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/inbound-outbound/images/download.svg) ](https://bintray.com/cjww-development/releases/inbound-outbound/_latestVersion)

inbound-outbound
====================


### Http.scala
Can be injected into a class that needs to make calls out to external services. 

For all these methods you need an implicit Request[_] in scape. 

GET needs an implicit Json Reads[T] in scope.

POST, PUT and PATCH need an implicit Json OWrites[T] in scope.

```scala
    //HEAD and GET example
    case class TestModel(str: String, int: Int)
    implicit val reads = Json.reads[TestModel]
    
    class ExampleConnector @Inject()(http: Http) {
      def exampleHeadRequest: Future[WSResponse] = {
        http.HEAD("/example/url")
      }
    
      def exampleGetTestModel: Future[TestModel] = {
        http.GET[TestModel]("/example/url")
      }
    }
```

```scala
    //POST, PUT and PATCH example
    case class TestModel(str: String, int: Int)
    implicit val writes = Json.writes[TestModel]
    
    implicit val writer: BodyWritable[TestModel] = {
   	 BodyWritable(model => InMemoryBody(ByteString(Json.toJson(model).toString())), "application/json")
  	}
        
    class ExampleConnector @Inject()(http: Http) {
      def examplePost: Future[WSResponse] = {
        http.POST[TestModel]("/example/uri", TestModel("abc", 616))
      }
      
      def examplePut: Future[WSResponse] = {
        http.PUT[TestModel]("/example/uri", TestModel("abc", 616))
      }
      
      def examplePatch: Future[WSResponse] = {
        http.PATCH[TestModel]("/example/uri", TestModel("abc", 616))
      }
    }
```

```scala
    class ExampleConnector @Inject()(http: Http) {
      def exampleDelete: Future[WSResponse] = {
        http.DELETE("/example/uri")
      }
    }
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
