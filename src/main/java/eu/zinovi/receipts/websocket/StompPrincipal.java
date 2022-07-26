package eu.zinovi.receipts.websocket;

import java.security.Principal;

class StompPrincipal implements Principal {
    private final String name;

    StompPrincipal(String name) {
        this.name = name;
//        System.out.println("StompPrincipal constructor User name: " + name);
    }

    @Override
    public String getName() {
//        System.out.println("StompPrincipal getter User name: " + name);
        return name;
    }
}
