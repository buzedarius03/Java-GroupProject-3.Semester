package at.tugraz.oop2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Relation {
    private long id;
    private List<Member> members;
    private Map<String, String> tags;

    public Relation(long id) {
        this.id = id;
        this.members = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void addMember(String type, long ref, String role) {
        members.add(new Member(type, ref, role));
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    private class Member {
        private String type;
        private long ref;
        private String role;

        public Member(String type, long ref, String role) {
            this.type = type;
            this.ref = ref;
            this.role = role;
        }
    }
}
