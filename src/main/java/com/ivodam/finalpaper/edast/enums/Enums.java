package com.ivodam.finalpaper.edast.enums;

public class Enums {

    public enum Roles {
        ROLE_ADMIN("Admin"),
        ROLE_USER("User"),
        ROLE_EMPLOYEE("Employee");

        public final String label;

        Roles(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return label;
        }

    }
}
