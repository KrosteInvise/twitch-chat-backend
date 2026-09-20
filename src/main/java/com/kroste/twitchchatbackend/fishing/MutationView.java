package com.kroste.twitchchatbackend.fishing;

public record MutationView(
        String id,
        String name
) {
    public static MutationView from(Mutation mutation) {
        if (mutation == null) {
            return null;
        }
        return new MutationView(mutation.id(), mutation.name());
    }

    public static MutationView from(String id, String name) {
        if (id == null || name == null) {
            return null;
        }
        return new MutationView(id, name);
    }
}
