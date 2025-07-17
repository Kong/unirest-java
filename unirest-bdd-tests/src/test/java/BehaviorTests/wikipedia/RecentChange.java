/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package BehaviorTests.wikipedia;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$schema",
    "meta",
    "id",
    "type",
    "namespace",
    "title",
    "title_url",
    "comment",
    "timestamp",
    "user",
    "bot",
    "notify_url",
    "minor",
    "patrolled",
    "length",
    "revision",
    "server_url",
    "server_name",
    "server_script_path",
    "wiki",
    "parsedcomment"
})

public class RecentChange {

    @JsonProperty("$schema")
    private String $schema;
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("namespace")
    private Long namespace;
    @JsonProperty("title")
    private String title;
    @JsonProperty("title_url")
    private String titleUrl;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("user")
    private String user;
    @JsonProperty("bot")
    private Boolean bot;
    @JsonProperty("notify_url")
    private String notifyUrl;
    @JsonProperty("minor")
    private Boolean minor;
    @JsonProperty("patrolled")
    private Boolean patrolled;
    @JsonProperty("length")
    private Length length;
    @JsonProperty("revision")
    private Revision revision;
    @JsonProperty("server_url")
    private String serverUrl;
    @JsonProperty("server_name")
    private String serverName;
    @JsonProperty("server_script_path")
    private String serverScriptPath;
    @JsonProperty("wiki")
    private String wiki;
    @JsonProperty("parsedcomment")
    private String parsedcomment;

    @JsonProperty("$schema")
    public String get$schema() {
        return $schema;
    }

    @JsonProperty("$schema")
    public void set$schema(String $schema) {
        this.$schema = $schema;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("namespace")
    public Long getNamespace() {
        return namespace;
    }

    @JsonProperty("namespace")
    public void setNamespace(Long namespace) {
        this.namespace = namespace;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("title_url")
    public String getTitleUrl() {
        return titleUrl;
    }

    @JsonProperty("title_url")
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("bot")
    public Boolean getBot() {
        return bot;
    }

    @JsonProperty("bot")
    public void setBot(Boolean bot) {
        this.bot = bot;
    }

    @JsonProperty("notify_url")
    public String getNotifyUrl() {
        return notifyUrl;
    }

    @JsonProperty("notify_url")
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @JsonProperty("minor")
    public Boolean getMinor() {
        return minor;
    }

    @JsonProperty("minor")
    public void setMinor(Boolean minor) {
        this.minor = minor;
    }

    @JsonProperty("patrolled")
    public Boolean getPatrolled() {
        return patrolled;
    }

    @JsonProperty("patrolled")
    public void setPatrolled(Boolean patrolled) {
        this.patrolled = patrolled;
    }

    @JsonProperty("length")
    public Length getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(Length length) {
        this.length = length;
    }

    @JsonProperty("revision")
    public Revision getRevision() {
        return revision;
    }

    @JsonProperty("revision")
    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    @JsonProperty("server_url")
    public String getServerUrl() {
        return serverUrl;
    }

    @JsonProperty("server_url")
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @JsonProperty("server_name")
    public String getServerName() {
        return serverName;
    }

    @JsonProperty("server_name")
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @JsonProperty("server_script_path")
    public String getServerScriptPath() {
        return serverScriptPath;
    }

    @JsonProperty("server_script_path")
    public void setServerScriptPath(String serverScriptPath) {
        this.serverScriptPath = serverScriptPath;
    }

    @JsonProperty("wiki")
    public String getWiki() {
        return wiki;
    }

    @JsonProperty("wiki")
    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    @JsonProperty("parsedcomment")
    public String getParsedcomment() {
        return parsedcomment;
    }

    @JsonProperty("parsedcomment")
    public void setParsedcomment(String parsedcomment) {
        this.parsedcomment = parsedcomment;
    }

}
