# redis-example
Simple project for writing notes using Redis, Thymeleaf, MongoDB and method writeString by class Files.

Stack: Maven, Redis, Jedis, Thymeleaf, MongoDB, Lombok, JDK 11 (and upper).

## Step 1
Install necessary databases.
1.1. Set up server Redis.
```
brew install redis
```
Then start server.
```
redis-server
```
1.2. Set up MongoDB.

Find the MongoDB tap.
```
brew tap mongodb/brew
```
Install MongoDB.
```
brew install mongodb-community
```
Then start service.
```
brew services run mongodb-community
```

## Step 2
Add necessary dependencies.
```.xml
 <dependencies>
        <!-- redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.3.0.RELEASE</version>
            <exclusions>
                <exclusion>
                    <groupId>io.lettuce</groupId>
                    <artifactId>lettuce-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
            <version>2.2.7.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
            <version>2.3.0.RELEASE</version>
        </dependency>
```

## Step 3
Fill the file "application.properties".
```.properties
app.redis.host=localhost
app.redis.port=6379
app.redis.database=0
app.redis.counter=test

server.port=9080

spring.data.mongodb.uri=mongodb://localhost:27017/notes
```

## Step 4
Set up configuration beans for client implementation of Redis - Jedis.
```.java
@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database);
        return new JedisConnectionFactory(config);
    }
```

## Step 5
Since the reason of using Redis in this project is the atomic counter that it supports, make a helper class with RedisAtomicLong counter.
```.java
@Repository
@RequiredArgsConstructor
public class JedisRepository {

    private final RedisConnectionFactory redisConnectionFactory;

    RedisAtomicLong redisAtomicLong;

    @Value("${app.redis.counter}")
    private String counter;

    @PostConstruct
    public void init() {
        redisAtomicLong = new RedisAtomicLong(counter, redisConnectionFactory);
    }

    /**
     * Atomically increment by one the current value.
     *
     * @return the next value
     */
    public long get() {
        return redisAtomicLong.incrementAndGet();
    }
}
```

## Step 6
Create a template for note xml file using the following information:
- the changing fields "Author", "Title", "Content" would be received in the request;
- the “Date” field will be created as the current date of the request;
- the "NoteId" field would be received from Redis.
```.xml
<Documents>
    <Document>
        <Note>
            <NoteId>[(${noteId})]</NoteId>
            <Author>[(${authorName})]</Author>
            <Tittle>[(${title})]</Tittle>
            <Content>[(${content})]</Content>
            <Date>[(${date})]</Date>
        </Note>
    </Document>
</Documents>
```

## Step 7
Create a helper class to customize field changes in the template.
```.java
@Component
@RequiredArgsConstructor
public class XmlHelper {

    private final TemplateEngine templateEngine;
    
    public String makeXml(Note note, String date) {
        Context context = new Context();
        context.setVariable("noteId", note.getId());
        context.setVariable("authorName", note.getAuthor());
        context.setVariable("title", note.getTitle());
        context.setVariable("content", note.getContent());
        context.setVariable("date", date);
        return templateEngine.process("NoteTemplate.xml", context);
    }
}
```

## Step 8
Create a helper class for writing data to an xml file using method writeString by class Files.
```.java
@Slf4j
@Repository
@RequiredArgsConstructor
public class XmlOutRepository {

    private final XmlHelper xmlHelper;

    public synchronized void createAndSave(Note note, String currentDate) {

        String xml = xmlHelper.makeXml(note, currentDate);
        String fileName = note.getTitle() + "-" + note.getId()+".xml";
        Path dir = Paths.get("imports", currentDate);
        Path pathToFile = dir.resolve(fileName);

        try {
            if (Files.notExists(pathToFile)) {
                Files.createDirectories(dir);
                Files.createFile(pathToFile);
            }
            Files.writeString(pathToFile, xml, StandardCharsets.UTF_8);
            log.info("File saved.");
        } catch (IOException e) {
            log.error("Error writing to file.", e);
        }
    }
}
```

## Step 9
Create an entity - Note.
```.java
@Data
@Document(value = "notes")
public class Note {

        @Id
        private Long id;

        private String author;
        private String title;
        private String content;

}
```

## Step 10
Create a rest controller. 
```.java
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    private final XmlOutRepository xmlOutRepository;

    @PostMapping
    public Note create(@RequestBody Note note){
        String date = LocalDate.now().toString();
        Note neededNote = noteService.saveOrUpdate(note);
        xmlOutRepository.createAndSave(neededNote, date);
        return neededNote;
    }
    @GetMapping
    public Iterable<Note> getAll(){
        return noteService.findAll();
    }
    @GetMapping("/{id}")
    public Note getById(@PathVariable Long id){
        return noteService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        noteService.deleteById(id);
    }
}
```