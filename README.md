# Spring Anatomy

## Create a Spring Boot project

We will use Spring Boot, because it is a more productive project template that uses Spring Framework.

The easiest way to start our Spring Boot project is to download the Maven template from [Spring Initializer] [spring_initializer].

Your pom.xml will contain the following section:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.2.RELEASE</version>
</parent>
```

On top of that, we will add the following starter packages provided by Spring Boot, as regular dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

We will add Spring Boot devtools as well - this will watch the sources and refresh the application whenever we save a relevant file:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

As a prerequisite, you should have a MySQL DB ready -- to connect our JPA, DB agnostic repositories to the actual DB, we will add the MySQL driver:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

Notice that we did not mention the versions for the packages thus far -- these are contained in the parent pom of the Spring Boot application, which is a convenient way to be sure that only compatible dependencies are used whenever we change the framework version.

Let's ask a couple of important questions at this stage:

### Spring is a framework. But what is a framework?

A framework is a set of tools and libraries which are commonly used in a lot of projects.
Since the goal is to REUSE as much code as possible with as little effort as possible, such tools are often bundled together, as is the case with Spring.

### Spring is an IoC container. What is IoC and what is an IoC container?

[Inversion of control][wiki_ioc] is a design principle in which the custom written parts of the code receive the flow of control from a larger framework.
What does this mean?
We have already acknowledged that a lot (if not most) of the code in our application is reused in the shape of some libraries or a framework.
So, the classical way of building our application would be like this:

```java
public class ServerApp{
    public static void main(String[] args){
	SocketConnector sk=new SocketConnector(...);
	Server sv=new Server(sk, ...);
	try(MySQLConnection conn=new MySQLConnection(...)){
	    while(sv.listen){
		Request rq=sv.parseRequest();
		try(MySQLPreparedStatement st=conn.createPreparedStatement(...)){
		    sc.set(1,rq.get(...));
		    sc.set(2,rq.get(...));
		    ResultSet qr=st.executeQuery(...);
		    Response re=new Response(qr.get(...));
		    sv.sendResponse(re.marshall());
		}
	    }
	}
    }
}
```

This implementation has a few flaws:

- It is tightly coupled with the technology we have chosen for ... pretty much everything (Socket, Server, MySQL). If we change our minds on any of these, we need to re-write almost from scratch.
- It is not focused on a single task, because it handles a lot of (otherwise reusable) sub-tasks (creating a socket, starting a server, querying the DB). Even if we put those tasks in different classes, we still need to instantiate those classes, with all the necessary parameters.
- It relies on a very specific set of assumptions, instead of abstract contracts: we must connect to a socket like so, we must prepare a statement with a query in this dialect, etc.
- The execution of our task (a web server) is in fact represented by this implementation. Although most of the libraries are present and the mechanism is quite common, we cannot start our web server without writing this boilerplate.

To address these flaws, modern frameworks have come up with an idea: since most of these common mechanisms are already written anyway, why not start them up front, instead of your custom code? Then, you can plug in your code somewhere on the pipe, in order to add your business logic.
You want a web server? We have one. You just need to add controller logic.
You want a DB connection with an ORM on top? No problem. You just add the domain objects (aka the tables) and the queries. No more preparing statements. No more closing connections.

Think of IoC as mass production. Before mass production, to get clothes for a fancy dinner, you'd have to go to a tailor.
Several fitting sessions and about a month later, you would have your dress. With mass production, you just pick ready made clothes of the shelf. All you have to do is choose your size.
In software terms, you no longer need to custom write EVERYTHING. You just parameterize existing components and add on top what is specific to your business logic.

The department store where you buy ready made clothes is, in our case, an IoC container. And Spring is one of those.

Now, in the early days of Spring, "picking your clothes" meant actually specifying each and every one of them in an xml file.
Today's Spring Boot has two charming improvements to that procedure:

- Classpath scanning: you don't need to specify that you need Spring to instantiate a MySQL driver, an ORM context, a Tomcat server, etc. You just put them in your classpath. If Spring detects that they are required, it will instantiate them automatically.
- Defaults, or as they call it "convention over configuration": you don't have to parameterize everything. If you don't, Tomcat will start on port 8080, MySQL will connect to localhost:3306, etc. You may configure only what's different.

### Yeah, but how does it do it?

While it may look like magic, it's actually quite simple.
The basic idea of the IoC container is to do what every department store does: maintain a tidy catalog of the available products.
Bring new products when they are in demand. Have an assistant ready to take you to the right shelf.

In Spring terms, the department store is called the "context".
You no longer run your application on scratch, you run it on a context.
The context is maintained by Spring, you don't need to worry about that.
All you need to worry about is making sure the context is supplied appropriately.
For example, if you require from the context a JPA repository, but you did not import any JPA dependency in your pom.xml, Spring cannot find it in the classpath.
To simplify, Spring's context is a Map<String,Object> containing all the components needed to run the application.
Since Spring has no way of knowing which components are needed, in the absence of a crystal globe, we tell Spring what we need, with the help of a design pattern called Dependency Injection.
This consists of two simple annotations, that come in a variety of shapes and flavors:

- `@Component` tells Spring that the class annotated as such needs to be instantiated on the context, because it may be required in other parts of our code.
- `@Autowired` tells Spring that the member or members to which this annotation refers need to be assigned automatically, by using an object already present in the context.

As you can imagine, this "wiring" process creates a tree of dependencies.
Any missing dependency will cause Spring framework to break -- which is actually a good thing, because you will know something is wrong from the start.

## Configuring your Spring application

We mentioned the ability to parameterize the Spring application.
While there is a [variety of means][spring_configuration] by which Spring Boot can be configured, for this exercise we will use an `application.yml` file placed in the root of our `src/main/resources` folder.

For the sake of demonstration, we will change the server port, configure the MySQL connection, and the ORM.
Please adjust the connection parameters to match your own configured DB. We will discuss some of these parameters later.

```yaml
server:
    port: 7799
spring:
    jpa:
        database: MYSQL
        database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
        hibernate:
            ddl-auto: create-drop
        properties:
            hibernate:
                connection:
                    charSet: UTF-8
                hbm2ddl:
                    import_files_sql_extractor: org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor
    datasource:
        url: jdbc:mysql://192.168.56.101/spring_training?reconnect=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false
        username: spring
        password: training
        driver-class-name: com.mysql.jdbc.Driver
        maxActive: 5
```

## Anatomy of the Spring context

The Spring application is now ready to start. Naturally, it has no code of our own, so, basically, it does not do anything interesting.
Since we are using a web server, let's give it something to do. We talked about Spring context, so let's see what it looks like.

For the web server in Spring's context, "work" means a "controller".
This is a class containing business logic that we can ask the web server to take care of.
To let Spring know that class is a controller, all we need to do is decorate it with the `@Controller` annotation, which is a kind of `@Component`.
We will go a bit further and decorate it with `@RestController`, which tells Spring additionally that it needs to try to marshal the return value of any method using whatever marshaller is available.
For example, since the package `spring-boot-starter-web` brings in the `Jackson` library, we will have our return values marshalled as json by default.
We will also decorate our class with `@RequestMapping` to let the framework know that we want all the methods in our class to be prefixed with `/api/context`.

We want to take a look at the context, so in order to do that we will wire it in as an attribute of our class (yes, the context itself can be wired in; since it is the first thing that Spring creates, we don't have to worry that it might be missing).
There are three ways to wire a component using Spring: field injection, method injection and constructor injection.
While there are business cases that may suggest one approach or another, in most situations there is an accepted [best practice][injection_best_practices]:

- Mandatory dependencies should be injected in the constructor, with the corresponding fields `private final`;
- Optional dependencies should be injected in setter methods.

The reason for this is the need to express clearly the contract of your class, even though the framework is smart enough to function correctly in the absence of such contract.
Basically, this allows the designer to decouple the class from the framework.
Perhaps we won't ever need to change the framework -- but we will definitely need to unit test our class.
Some of you may object at this point that the framework can be used for wiring even in testing, which is true.
But if we bring in the framework, that's no longer much of a unit test -- it's an integration test.

Coming back to our controller, we will autowire the `ApplicationContext` in the constructor. Notice that `ApplicationContext` is an interface.
Yes, Spring framework can do that. It will scan its context, looking for a unique implementation of `ApplicationContext`.
If there is none, it will crash. If there are more than one, it will crash.

Now let us write our controller method. We need to annotate it with `@RequestMapping` (just as we did the class) -- but we will use a shorthand, called `@GetMapping`, which is the same as `@RequestMapping(method="GET")`.
We could give our annotation a `value` as well, but we are happy with the class prefix: basically, a GET call to `/api/context` will invoke this method.
In web services, it is a good practice to be as specific and restrictive as possible, so we will tell our method that it should produce a json output, by setting `produces="application/json"`.
The method will return `List<String>`, so the output will be a json array. So, in the end, our method will look like this:

```java
@GetMapping(produces = "application/json")
    public List<String> getContextMembers() {
	return Arrays.asList(context.getBeanDefinitionNames());
    }
```

There are a couple of hidden things that Spring does for us:

- We know the first one: a GET call to `/api/context` will be routed to invoke our method and return the list of bean definition names, along with a 200 OK HTTP code;
- A call to any other path will return a 404 NOT_FOUND HTTP code (by default, it will also return a human readable error page)
- If we forgot to wire in the context, then our method will throw a `NullPointerException`, in which case the framework will return a 500 INTERNAL_SERVER_ERROR HTTP code, as well as a brief description of the problem. We can suppress that from the Spring configuration, if we don't want people to laugh at our silly mistakes (or learn about them and try to exploit them)
- If we send a POST call to `/api/context`, the framework will answer with a 405 METHOD_NOT_ALLOWED HTTP code
- If we send a GET call to `/api/context`, but we add an HTTP header `Accept:application/xml`, the framework will answer with a 406 NOT_ACCEPTABLE, because we said we would only deliver json

Please refer to the [Spring Web MVC][spring_web_mvc] documentation for further reading on the various kinds of magic that Spring can do in a controller.

## Domain classes

These are your DTOs, your POJOs. They allow you to work with records to and from the DB, in a meaningful, type safe manner.
Let's create two of these, `User` and `Article`, then link them with a relationship.
A few things to consider (all of the referred annotations are part of the `javax.persistence` package, so they are not specific to Spring):

- A domain class needs to be annotated with `@Entity`. We can specify as an argument the name of the table.
- A column in the table needs to be annotated with a column reference, such as `@Column`, `@Basic`, `@Id`, or a relationship reference (e.g. `@OneToMany`).
- An entity class must have one `@Id` annotated field.

So, without further ado, our `User` class will look like this:

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    @Basic(optional = false)
    private String username;

    @OneToMany(mappedBy = "author")
    private List<Article> articles = new LinkedList<>();

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public List<Article> getArticles() {
	return articles;
    }

    public void setArticles(List<Article> articles) {
	this.articles = articles;
    }

}
```

And our Article class will look like this:

```java
@Entity
public class Article {

    public enum ArticleType {
	FRONT_PAGE, BLOG
    };

    @Id
    @GeneratedValue
    private Integer id;

    @Basic(optional = false)
    private String title;

    @Basic
    private String text;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private ArticleType articleType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author")
    private User author;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public Date getPublishDate() {
	return publishDate;
    }

    public void setPublishDate(Date publishDate) {
	this.publishDate = publishDate;
    }

    public ArticleType getArticleType() {
	return articleType;
    }

    public void setArticleType(ArticleType articleType) {
	this.articleType = articleType;
    }

    public User getAuthor() {
	return author;
    }

    public void setAuthor(User author) {
	this.author = author;
    }

}
```

The relationship is bidirectional, which means we have access to the author from `Article` (and we called it explicitly by the name "author", because we don't like the default "author_id" conventional name) and also to the articles from `User`.

Please notice the hibernate `ddl-auto:create-drop` property in our `application.yml` file.
It tells the framework to create tables in our database automatically, based on the domain classes in our java project, then drop them when the application closes.
This is a neat feature while you are developing your application, but do remember to turn it off before deploying it in production.

So, every time we save a new change to our code, Spring Boot devtools will refresh the context, including a drop/create on the DB.
But what if we need some values inserted in those tables?
We can take care of those by placing an `import.sql` file in our `src/main/resources` folder, right next to our `application.yml`.
Speaking of which, please notice the hibernate `hbm2ddl: import_files_sql_extractor: org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor` setting, which tells Hibernate that our SQL statements may span over multiple lines.

So, let's add a small `import.sql`, like the one below:

```sql
insert into `user`(`username`) values ('Peter');
insert into `user`(`username`) values ('Lois');
insert into `user`(`username`) values ('Meg');
insert into `user`(`username`) values ('Chris');
insert into `user`(`username`) values ('Stewie');
insert into `user`(`username`) values ('Brian');

insert into `article`(`author`,`publish_date`,`article_type`,`title`,`text`)
select `id`,current_timestamp,'BLOG','Dear diary','Guess what I finally caught today' from `user` where `username`='Brian'
union select `id`,current_timestamp,'BLOG','The Art of War','Lois must die. It is the ultimate goal.' from `user` where `username`='Stewie'
union select `id`,current_timestamp,'FRONT_PAGE','Lipshit','Why cheap lipstick is bullshit.' from `user` where `username`='Meg';
```

## Repositories and CRUD

Normally, these should be two separate chapters: Repositories are the DAO classes, that contain methods allowing us access to the DB, while the CRUD is defined in the controllers.

But as we have a bit of experience already working with controllers, we will take a "top-down" approach on the matter, starting with the REST calls.

Let's start with the User CRUD.
As with the `ContextController`, we will create a controller class, called `UserController` and annotate it with `@RestController` and `@RequestMapping("/api/user")`.

First, we want to obtain a list of users -- so our first method (annotated with `@GetRequest(produces="application/json")`) will be `public List<User> getUsers()`.
We need to get these users from the DB somehow. Luckily for us, Spring has a lovely mechanism that allows us to get it done in just a couple of lines.

We create an interface called `UserRepository`, annotate it with `@Repository`, which is another kind of `@Component`, and make it extend `JpaRepository<User,Integer>`.
This expresses a contract for a standard repository working with entities as defined in the `User` class, whose id is of type `Integer`.

Now, all we have to do is implement our interface, that is write MySQL specific code for `findAll`, `findById`, `save`, `delete`, etc.

Just kidding :) . Spring will do all those for us, we don't have to implement anything.

But how???

The mechanism that Spring uses is a reflection design pattern called [dynamic proxy][java_proxy].
While this may all seem very complicated (perhaps not for PHP developers who know about "magic methods") -- all you need to know to create your own proxies is that Java gives you a mechanism to implement an interface at runtime.
Since there are not all that many ways to do that, the reference article samples should be enough.
All you need to know to use Spring's repository proxies is ... well, nothing: just use the interface as it is.

So, we will just wire our newly created interface (or just `JpaRepository<User,Integer>` -- yes, Spring knows to inject beans based on generic arguments).
And, voila: now we can return `userRepository.findAll()` in our controller method. Point your browser to `/api/user` to get the Family members.

Oups. We got a `StackOverflow`. How could this happen to us? Well, the marshaller just went to `User` and found a `List<Article>`.
So, it decided to marshal `Article` as well. Then it went to `Article` and found a `User`. So, it decided to marshal `User`. Again. You get the point.
To prevent this from happening, we will add an annotation which is specific to the marshaller, which is `@JsonIgnore` on top of the `getArticles()` method in `User`.
This will prevent the marshaller from pursuing that branch. If in some case we actually want to have the sub-collection expanded, Jackson has an even smarter mechanism, based on `@JacksonManagedReference` and `@JacksonBackReference`.
Read all about these and bookmark [this website][baeldung_jackson_infinite] while you're at it.

Let's repeat the User CRUD process so far for the `ArticleController` and `ArticleRepository` classes.

Now, a normal question arises: what good is our `findAll` if there are way too many records?
We should offer the REST client a way to limit the amount of data returned, which is commonly known as "paging".
Upon a closer inspection, you will notice that the default repository also has a `Page<T> findAll(Pageable pg)` method.
What's more interesting, the `Pageable` argument can be passed as an argument in the controller method, like so:

```java
@GetMapping(produces = "application/json")
public Page<Article> getArticles(@PageableDefault(page = 0, size = 10) Pageable p) {
    return this.articleRepository.findAll(p);
}
```

The `Pageable` argument is based on some request parameters sent from the web: for example, a call to `/api/article?page=1&size=2` will produce the second page, with a page size of 2.
We added the `@PageableDefault` annotation to control what is used if no request parameters are sent.
Also take note that the `Pageable` interface also controls the sort direction.

What else could a REST client possibly want? A filter maybe. Like, some kind of search? (pun intended, we will need a `LIKE` SQL operator).
Let's assume the REST contract specifies that the list of articles must be filtered with a `search` optional argument which should refer to either the `author` or the `title` fields.

We can no longer use the default `JpaRepository` proxy, we will need to enrich it with our own method, containing the search.

While there are many ways of doing so, that you can read about [here][spring_data_jpa], we will choose to create a method in our `ArticleRepositor`, with a `@Query` annotation on top.

The `@Query` annotation lets us write an actual SQL query, either in the targeted DB Dialect, by specifying `native=true` (which we don't want, because we don't like to be coupled tightly with that technology), or in JPQL, which is a dialect-agnostic language, that can be translated into any dialect by the JPA implementation (in our case Hibernate).

Our `ArticleRepository` will now contain the additional method:

```java
@Query("select a from Article a where a.author.name like %:search% or a.title like %:search%")
public Page<Article> search(@Param("search") String search, Pageable p);
```

Don't worry about implementing this either. Spring will proxy your method just as it proxies its default methods. After all, it has all the information it needs.

After you save it, you will notice that Spring crashes: `could not resolve property: name of: com.computaris.springtraining.domain.User`.
Indeed, we made a mistake: `select a from Article a where a.author.name`, but in our `User` class, there is no `name`. We were thinking about `username`, so let's correct the mistake.
Just be aware that the `@Query` JPQL is not just a flat string, it gets parsed as a DSL when the application starts and will not allow syntax mistakes (which, again, is a good thing).

Our `ArticleController` GET list implementation will also change:

```java
@GetMapping(produces = "application/json")
public Page<Article> getArticles(@PageableDefault(page = 0, size = 10) Pageable p,
	@RequestParam(defaultValue = "", required = false, name = "search") String search) {
    return this.articleRepository.search(search,p);
}
```

We added the `@RequestParam` annotation, mentioning that we don't require `search` to be present always, but if it is not, we default it to the empty string, to avoid passing `null` to the repository.
You can try our achievement by pointing your browser to `/api/article?search=bria` for example.

## The full CRUD

For the `find` issue, all was nice and easy. What about the other letters in CRUD -- specifically Create, Update and Delete?

First of all, these will need to be connected to sensible HTTP endpoints, based on a generally accepted [convention][wiki_rest].

For example, for `UserController`, these would like like this:

```java
@PostMapping(consumes = "application/json")
public void createUser(@RequestBody User user) {
    this.userRepository.save(user);
}

@PutMapping(value = "/{userId}", consumes = "application/json")
public void updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) {
    User u = this.userRepository.findOne(userId);
    if (u == null) {
	u = user;
    } else {
	u.setUsername(user.getUsername());
    }
    this.userRepository.save(u);
}

@DeleteMapping("/{userId}")
public void deleteUser(@PathVariable("userId") Integer userId) {
    this.userRepository.delete(userId);
}
```

Notice the shorthand annotations `@PostMapping`, `@PutMapping` and `@DeleteMapping`, restricting the HTTP method that can be used.
Also, since now we are receiving content from the REST client, instead of providing content, we will restrict the inbound format, by using the `consumes` argument.

Please notice also the `@PathVariable` and `@RequestBody` annotations. We used the `@RequestParam` before, to introduce the `search`. In an HTTP request, that comes after `?`.
The path variable is a dynamic part of the path itself, for example `/api/user/3`. The request body is the content, or payload, in case of a POST or PUT HTTP request.
In our case, we expect the marshaller to be able to unmarshal a `User` object from the incoming POST or PUT -- otherwise it will automatically return a 400 BAD_REQUEST HTTP code.

If we create a new `User` in our DB, it is possible to send in also some articles, as a json array of objects that can be unmarshalled as `Article`.
In that case, we expect the ORM to do two operations: one, save the `User`, two, save the `Articles`.
As an in-between operation, it should retrieve the id of the new `User` in order to fill in the `Article` foreign key, which would otherwise raise an exception, due to the `optional=false` argument we have on the `author`.

Additionally, we would like all these operations to happen as a TRANSACTION, so we will annotate our POST handler with `@Transactional`.

### Transactional

A few words on this topic. Firstly, what is [Transactional][wiki_transactional]?
It's a design pattern where several actions of information processing are treated as an indivisible block.
That means that they either all succeed, or all fail.
A state where an arbitrary part succeeded and the rest failed is called inconsistent and, in transactional processing, should be avoided at all costs.
In the case of our articles, we like this, because there would be significant effort involved in repairing an inconsistent state, once, for example, the `User` and some of the `Article` objects are saved, but others are not.
In other case, we cannot live without this: imagine a bank transaction in which you must take some money from one account and deposit it into another.
An inconsistent state would mean either that you took money but never put it anywhere, or that you deposited money that do not come from anywhere.

Did we mention that we solved this problem in Spring by just adding `@Transactional` to the method where we wanted it?
But how? It sounded so complicated in that Wikipedia article...
The answer is: by using [AOP][wiki_aop].

Transactionality is a cross cutting concern and, in our case, `@Transactional` is the pointcut we need to implement this business logic.
Did I say "we"? I meant Spring.
There is nothing that the framework does not know how to do already. In pseudo code, it needs to:

```
open transaction on DB engine
try
    execute our code
    commit changes to DB
catch
    rollback changes
finally
    close transaction
```

## Swagger

We already have a whole lot of methods available, and that's just a couple of domain classes.
REST is a contract. For a real life application, we will usually work in parallel with another team that uses this contract to implement a client, such as a Front End application.
We need to communicate this contract to them efficiently.

We can do that with the help of another nice tool, called Swagger.

To use it, first we need to add the dependency to the pom.xml:

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.6.1</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.6.1</version>
    <scope>compile</scope>
</dependency>
```

While Swagger has its own set of annotations, Springfox is an adaptation that works very well with just Spring MVC's annotations (of course, you can document further by adding Swagger annotations on top of those)

To create the necessary endpoint in the Spring Boot application, we need to configure a bean in any `@Configuration` class, including the main class:

```java
@Bean
public Docket newsApi() {
    return new Docket(DocumentationType.SWAGGER_2)
	    .groupName("api")
	    .apiInfo(apiInfo())
	    .select()
	    .paths(regex("/api.*"))
	    .build();
}

private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
	    .title("Spring training")
	    .description("Anatomy of a Spring application")
	    .contact(new Contact("Valentin Raduti", "http://www.computaris.com", "valentin.raduti@computaris.com"))
	    .license("Commercial License")
	    .version("1.0")
	    .build();
}
```

Now, just point your browser to `/swagger-ui.html` and enjoy Swagger's nice presentation of your REST service, including the possibility to test it live.

## Security

Enter [Spring Security][spring_security]. This project uses OAuth2 authorization, which is arguably the most complex large-scale standard, but also the most flexible, since it allows your application to authenticate clients via Facebook, Google, or any other public provider.

Spring security works by filtering requests and allowing through those that match certain conditions.
The principle of OAuth2 is that the authorization server is independent from the actual application. 
This allows OAuth2, for example to function as a single sign on system.
In our case, the authorization server and the application will be placed in the same project.
The two configurations will be separate however, to allow you to easily separate them if you need to.

We have three filter requirements:

- The authorization server filter must allow through OAuth requests
- Both the authorization server and the resource server (the application) must allow through public information (such as the Swagger API docs)
- The resource server must allow through protected information ONLY to authenticated users. This application will allow all protected information to all authenticated users. Note that you can protect EACH request specifically to allow only certain users.

### OAuth2

Since theoretically OAuth2 is an independent authorization server that can be used by multiple consumers (applications), it has an additional layer of authentication.

Suppose I am an application that wants to use the OAuth2 server. The OAuth2 needs to give me a `clientId` and a `clientSecret` that I will attach to the authorization requests.

Authenticating these is done here:

```
@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
```

The filter itself is:

```
 @Override
    protected void configure(HttpSecurity http) throws Exception {
	http
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.formLogin().disable()
		.authorizeRequests()
		/**
		 * These permissions...
		 */
		.antMatchers("/v2/**").permitAll()
		.antMatchers("/webjars/**").permitAll()
		.antMatchers("/swagger-ui.html").permitAll()
		.antMatchers("/swagger-resources/**").permitAll()
		/**
		 * ...should be removed in production
		 */
		.antMatchers("/public/**").permitAll()
		.antMatchers("/oauth/**").permitAll();
```

Read more [here][oauth2] about the recommended flows.

### CORS 

Cross Origin Resource Sharing is a way of restricting client access to server hosted resources. 
For example, if I have a traditional web application, where the FE and BE are on the same domain (e.g. PHP, JSP), all non-user requests for resources will originate from the same domain.
An anchor pointing outside the domain, for example, needs the user to click it -- and that will move the browser to the other location.
But a script on my page that loads data via AJAX will load it from the same domain, unless it has some permission to do otherwise.
A script that runs on facebook.com cannot load data from google.com unless google.com agrees to provide a CORS filter allowing facebook.com scripts to access that data.

In our application, we will be generous and allow anyone to access our data. But not in production.

```
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");//<-- this is where we allow any origin
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");//<-- this is where we allow any operation
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, content-type");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
```

We used this opportunity to auto-handle the OPTIONS requests.
Keep watching Spring [docs](https://spring.io/blog/2015/06/08/cors-support-in-spring-framework) on this matter -- they may have a more elegant solution for this anytime soon.

## Testing

As a rule of thumb, if you actually need the framework for your tests to work, then you are writing integration tests.
If you don't, then you are writing unit tests.

### Unit testing

Remember dependencies injected in the constructor? They help a lot when writing unit tests -- assuming that the arguments in the constructors are interfaces.
Why? Because you can satisfy them as you would if there was no framework, except instead of true implementations, you would use mock implementations.

For example, to test our `UserController`, we could have something like this:

```java
public class UserControllerTest {

    private final UserController mockController = new UserController(AnyWayIWantToMockMyUserRepository);

    @Test
    public void testGetUsers() {
	Page<User> re = this.mockController.getUsers(new PageRequest(0, 10));
	Assert.assertNotNull(re);
	Assert.assertEquals(re.getContent().size(), 10);
    }
}
```

### Integration testing

If you want a more comprehensive test of your business logic, as placed in the wider perspective, you can bring in the framework wiring, just for testing.
There are multiple angles that are allowed by Spring, as you can read more [here][spring_test]. For example, to test our web controller, we would write something like this:

```java
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@EnableSpringDataWebSupport
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetUsers() throws Exception {
	this.mvc.perform(get("/api/user").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	this.mvc.perform(get("/api/user").accept(MediaType.APPLICATION_XML))
		.andExpect(status().isNotAcceptable());
    }
}
```

Notice the `@EnableSpringDataWebSupport` annotation, which allows us to use the `Pageable` interface in the controller method.

You may also notice that this test requires the Spring context to be booted up and shut down, which takes substantially more time than the unit test.

## References

- [Spring Initializer][spring_initializer]
- [Spring Web MVC][spring_web_mvc]
- [Spring Data JPA][spring_data_jpa]
- [Spring Boot External Configuration options][spring_configuration]
- [Spring Security][spring_security]
- [Spring Test][spring_test]
- [Java Dynamic Proxies][java_proxy]
- [Peter Daum @ Java Code Geeks aobout dependency injection best practices][injection_best_practices]
- [OAuth2][oauth2]
- [Baeldung: Jackson bidirectional relationships][baeldung_jackson_infinite]
- [Wikipedia: Inversion of Control][wiki_ioc]
- [Wikipedia: REST conventions][wiki_rest]
- [Wikipedia: Transactional processing][wiki_transactional]
- [Wikipedia: AOP][wiki_aop]

[spring_initializer]: https://start.spring.io/ (Spring Initializer)
[baeldung_jackson_infinite]: http://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion (Baeldung: Jackson bidirectional relationships)
[spring_web_mvc]: https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html (Spring Web MVC)
[spring_data_jpa]: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ (Spring Data JPA)
[spring_configuration]: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html (Spring Boot External Configuration options)
[spring_security]: https://projects.spring.io/spring-security/
[spring_test]: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html (Spring Test)
[java_proxy]: https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html (Java Dynamic Proxies)
[oauth2]: https://oauth.net/2/
[injection_best_practices]: https://www.javacodegeeks.com/2015/01/dependency-injection-field-vs-constructor-vs-method.html (Peter Daum @ Java Code Geeks aobout dependency injection best practices)
[wiki_ioc]: https://en.wikipedia.org/wiki/Inversion_of_contro (Wikipedia: Inversion of Control)
[wiki_rest]: https://en.wikipedia.org/wiki/Representational_state_transfer#Relationship_between_URL_and_HTTP_methods (Wikipedia: REST conventions)
[wiki_transactional]: https://en.wikipedia.org/wiki/Transaction_processing (Wikipedia: Transactional processing)
[wiki_aop]: https://en.wikipedia.org/wiki/Aspect-oriented_programming (Wikipedia: AOP)