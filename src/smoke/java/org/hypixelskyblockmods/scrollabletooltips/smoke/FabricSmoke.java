package org.hypixelskyblockmods.scrollabletooltips.smoke;

import com.google.gson.JsonParser;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Runs production Fabric transformations without starting a game or login. */
public final class FabricSmoke implements PreLaunchEntrypoint {
    @Override public void onPreLaunch() {
        if (!Boolean.getBoolean("scrollabletooltips.smoke")) throw new IllegalStateException("Development checks only");
        try {
            var loader = getClass().getClassLoader();
            var input = loader.getResourceAsStream("scrollabletooltips.mixins.json");
            var config = JsonParser.parseString(new String(input.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            var entries = config.getAsJsonArray("client");

            for (var entry : entries) {
                var name = config.get("package").getAsString().replace('.', '/') + "/" + entry.getAsString().replace('.', '/');
                var node = new ClassNode();
                new ClassReader(loader.getResourceAsStream(name + ".class")).accept(node, 0);
                var annotations = new ArrayList<AnnotationNode>();
                if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations);
                if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations);
                var annotation = annotations.stream().filter(a -> a.desc.endsWith("/Mixin;")).findFirst().orElseThrow();
                for (int i = 0; i < annotation.values.size(); i += 2) {
                    var key = annotation.values.get(i);
                    if (!key.equals("value") && !key.equals("targets")) continue;
                    for (var target : (List<?>) annotation.values.get(i + 1)) {
                        var targetName = target instanceof Type type ? type.getClassName() : target.toString();
                        var transformed = Class.forName(targetName, false, loader);
                        transformed.getDeclaredMethods(); // Forces verification of injected handler signatures.
                        System.out.println("SCROLLABLETOOLTIPS_SMOKE_TRANSFORMED " + targetName);
                    }
                }
            }
            System.out.println("SCROLLABLETOOLTIPS_SMOKE_PASSED");
            System.exit(0);
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        }
    }
}
