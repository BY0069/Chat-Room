package Entity;

import java.util.Objects;

public class Message {
    private Integer from;
    private Integer to;
    private Integer type;
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from) && Objects.equals(to, message.to) && Objects.equals(type, message.type) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, type, content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

