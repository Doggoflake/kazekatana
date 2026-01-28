package net.phospherion.kazekatana.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.phospherion.kazekatana.KazeKatana;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MCMetaProvider implements DataProvider {

    private final Map<String, ModItemModelProvider.AnimationData> animatedItems;
    private final PackOutput output;

    public MCMetaProvider(PackOutput output, Map<String, ModItemModelProvider.AnimationData> animatedItems) {
        this.output = output;
        this.animatedItems = animatedItems;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        // PackOutput root for RESOURCE_PACK (often already points to .../assets)
        Path packRoot = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).toAbsolutePath();

        // Derive project root from current working dir so we can write into src/main/resources
        Path workingDir = Path.of(".").toAbsolutePath().normalize();
        Path projectRoot = workingDir.getFileName() != null && "run-data".equals(workingDir.getFileName().toString())
                ? workingDir.getParent()
                : workingDir;
        if (projectRoot == null) projectRoot = workingDir;

        Path projectResourcesBase = projectRoot.resolve("src").resolve("main").resolve("resources").resolve("assets");

        for (var entry : animatedItems.entrySet()) {
            String texture = entry.getKey(); // e.g. "incindium_steel"
            ModItemModelProvider.AnimationData data = entry.getValue();

            // Build animation JSON
            JsonObject rootObj = new JsonObject();
            JsonObject anim = new JsonObject();
            anim.addProperty("frametime", data.frametime());
            anim.addProperty("interpolate", data.interpolate());

            JsonArray frames = new JsonArray();
            for (int i = 0; i < data.frames(); i++) frames.add(i);
            anim.add("frames", frames);
            rootObj.add("animation", anim);

            // Resolve packPath correctly: packRoot may already be the 'assets' folder.
            Path packPath;
            String packRootName = packRoot.getFileName() != null ? packRoot.getFileName().toString() : "";
            if ("assets".equalsIgnoreCase(packRootName)) {
                packPath = packRoot.resolve(KazeKatana.MOD_ID)
                        .resolve("textures")
                        .resolve("item")
                        .resolve(texture + ".png.mcmeta");
            } else {
                packPath = packRoot.resolve("assets")
                        .resolve(KazeKatana.MOD_ID)
                        .resolve("textures")
                        .resolve("item")
                        .resolve(texture + ".png.mcmeta");
            }

            // Also write into project resources so IDE and runClient see them immediately
            Path projectPath = projectResourcesBase.resolve(KazeKatana.MOD_ID)
                    .resolve("textures")
                    .resolve("item")
                    .resolve(texture + ".png.mcmeta");

            try {
                // Ensure parent directories exist
                Files.createDirectories(packPath.getParent());
                Files.createDirectories(projectPath.getParent());

                // Write both files
                String json = rootObj.toString();
                Files.writeString(packPath, json, StandardCharsets.UTF_8);
                Files.writeString(projectPath, json, StandardCharsets.UTF_8);

                // Verify writes
                if (!Files.exists(packPath)) {
                    throw new IOException("PackOutput file was not created: " + packPath);
                }
                if (!Files.exists(projectPath)) {
                    throw new IOException("Project resource file was not created: " + projectPath);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to write mcmeta for " + texture + " ; packPath=" + packPath + " ; projectPath=" + projectPath + " ; WORKING DIR = " + workingDir, e);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "KazeKatana MCMeta Provider (pack + project)";
    }
}
