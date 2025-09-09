{
  description = "Java Maven project with OpenJDK 21 dev shell";

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
          packages = [
            pkgs.openjdk21
            pkgs.maven
          ];

          shellHook = ''
            echo "Welcome to the Java dev shell (OpenJDK 21 + Maven)"
            echo "Run: mvn clean install"
          '';
        };
      });
}
