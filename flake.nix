{
  description = "JavaFX Maven project with OpenJDK 21 dev shell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in {
        devShells.default = pkgs.mkShell {
          packages = with pkgs; [
            openjdk21
            maven
            openjfx21
            xorg.libXxf86vm
            xorg.libXtst  # Add this for libXtst.so.6
            glib
            gtk3
            libglvnd
            mesa
            libthai
            cairo
            pango
            gdk-pixbuf
            atk
            wayland
          ];

          shellHook = ''
            # Set library paths for JavaFX native dependencies
            export LD_LIBRARY_PATH=${pkgs.lib.makeLibraryPath [
              pkgs.xorg.libXxf86vm
              pkgs.xorg.libXtst  # Add this
              pkgs.glib
              pkgs.gtk3
              pkgs.libglvnd
              pkgs.mesa
              pkgs.libthai
              pkgs.cairo
              pkgs.pango
              pkgs.gdk-pixbuf
              pkgs.atk
              pkgs.wayland
            ]}:$LD_LIBRARY_PATH
            
            # Hint for JavaFX to use GTK3
            export JAVA_OPTS="-Djavafx.platform=gtk"
            echo "JavaFX dev shell ready. Run with: mvn clean javafx:run"
          '';
        };
      });
}
