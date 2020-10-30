package org.rebioma.server.services;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rebioma.client.AscModelResult;
import org.rebioma.client.services.RebiomaModelService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.berkeley.mvz.rebioma.client.Model;
import edu.berkeley.mvz.rebioma.client.ModelService;
import edu.berkeley.mvz.rebioma.client.ModelSpec;

@SuppressWarnings("serial")
public class ModelServiceImpl extends RemoteServiceServlet implements
    ModelService, RebiomaModelService {

  private static final String BASE_PATH = "war/ModelOutput";

  private static final String BASE_URL = "http://localhost/ModelOutput";

  private static final String MODEL_OUTPUT = "/ModelOutput";

  private static String speciesPath(String species) {
    return String.format("%s/%s", BASE_PATH, speciesPathName(species));
  }

  private static String speciesPathName(String species) {
    String n = species.substring(0, 1).toUpperCase() + species.substring(1);
    if (n.contains(" ")) {
      String[] split = n.split(" ");
      n = String.format("%s_%s%s", split[0], split[1].substring(0, 1)
          .toLowerCase(), split[1].substring(1));
    }
    return n;
  }

  private final AscModelDb ascModelDb = DBFactory.getAscModelDb();

  public AscModelResult findModelLocation(String acceptedSpecies, int start,
      int limit, int startM, int limitM) {
    return ascModelDb.findAscModel(acceptedSpecies, start, limit, startM, limitM);
  }

  /*
   * private static String zipModel(String species) { try { String path =
   * String.format("%s/Climate", speciesPath(species));
   * 
   * File inDir = new File(path); File inDirPlots = new
   * File(String.format("%s/plots", path)); File zipFile = new
   * File(String.format("%s/%s.zip", path, speciesPathName(species))); if
   * (zipFile.exists()) { return zipFile.getPath(); } ZipOutputStream out = new
   * ZipOutputStream(new BufferedOutputStream( new FileOutputStream(zipFile)));
   * BufferedInputStream in = null; byte[] data = new byte[1000]; String files[]
   * = inDir.list(new FilenameFilter() { public boolean accept(File dir, String
   * name) { return !name.equals("ProjLayers") && !name.equals("EnvLayers");
   * 
   * } }); for (int i = 0; i < files.length; i++) { in = new
   * BufferedInputStream(new FileInputStream(inDir.getPath() + "/" + files[i]),
   * 1000); out.putNextEntry(new ZipEntry(files[i])); int count; while ((count =
   * in.read(data, 0, 1000)) != -1) { out.write(data, 0, count); }
   * out.closeEntry(); } data = new byte[1000]; files = inDirPlots.list(); for
   * (int i = 0; i < files.length; i++) { in = new BufferedInputStream(new
   * FileInputStream(inDirPlots.getPath() + "/" + files[i]), 1000);
   * out.putNextEntry(new ZipEntry(files[i])); int count; while ((count =
   * in.read(data, 0, 1000)) != -1) { out.write(data, 0, count); }
   * out.closeEntry(); } out.flush(); out.close(); return zipFile.getName(); }
   * catch (Exception e) { e.printStackTrace(); return null; } }
   */
  public Model getModel(ModelSpec spec) {
    String basePath = getServletContext().getRealPath("/ModelOutput");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (spec == null) {
      throw new NullPointerException("Spec was null");
    }
    String species = spec.getSpecies();
    if (species == null || species.length() < 1) {
      return null;
    }
    Model m = null;
    String path = speciesPathName(species);
    File f = new File(String.format("%s/%s/Climate/%s.html", basePath, path,
        path));
    if (f.exists() && f.isFile() && f.canRead()) {
      String url = String.format("%s/%s/Climate/%s.html", BASE_URL, path, path);
      // zipModel(species);
      String zipUrl = String.format("%s/%s/Climate/%s.zip", BASE_URL, path,
          path);
      String imageUrl = String.format(
          "%s/%s/Climate/plots/%s_ProjLayers_median.png", BASE_URL, path, path);
      m = Model.newInstance(url, "bar", zipUrl, imageUrl, spec);
    }
    return m;
  }

  public List<String> getModelClimateEras(String modelLocation) {
    String basePath = getServletContext().getRealPath(MODEL_OUTPUT);
    File modelDir = new File(basePath + "/" + modelLocation);
    List<String> climateEraDirectories = new ArrayList<String>();
    if (modelDir.exists() && modelDir.isDirectory()) {
      for (File file : modelDir.listFiles()) {
        if (file.isDirectory()) {
          climateEraDirectories.add(file.getName());
        }
      }
      // climateEraDirectories = Arrays.asList(modelDir.list());
      Collections.sort(climateEraDirectories);
    } else {
      climateEraDirectories = new ArrayList<String>();
    }
    return climateEraDirectories;
  }

  public List<String> getModelNames() {
    String basePath = getServletContext().getRealPath("/ModelOutput");
    List<String> results = new ArrayList<String>();
    for (File f : new File(basePath).listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        File f = new File(String.format("%s/Climate/%s.html", pathname
            .getPath(), pathname.getName()));
        return f.exists() && f.canRead() && f.isFile();
      }
    })) {
      results.add(f.getName().replace("_", " "));
    }
    Collections.sort(results);
    return results;
  }
}
