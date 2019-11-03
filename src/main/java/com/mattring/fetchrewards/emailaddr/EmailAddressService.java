package com.mattring.fetchrewards.emailaddr;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailAddressService {

    public static void main(final String[] args) throws IOException {

        final int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        final Javalin app = Javalin.create(config -> {
            config.addStaticFiles("pages/");
        }).start(port);

        app.post("/countUniqueAddrs", ctx -> handleCountUniqueAddrs(ctx));
    }

    static void handleCountUniqueAddrs(Context ctx) {
        final String[] addrListFieldValues = ctx.req.getParameterValues("addrList");
        if (addrListFieldValues == null || addrListFieldValues.length < 1) {
            ctx.result("ERROR: No 'addrList' fields were provided.");
        } else {

            // NOTE: forms may have multiple fields with the same name.
            final String addrList = Arrays.stream(addrListFieldValues).collect(Collectors.joining("\r\n"));
            final Set<String> uniqueAddrs = EmailAddressFunctions.findUniqueNormalizedEmailAddresses(addrList);
            ctx.result(Integer.toString(uniqueAddrs.size()));
        }
    }
}
