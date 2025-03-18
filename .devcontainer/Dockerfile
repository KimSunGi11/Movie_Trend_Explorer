FROM mcr.microsoft.com/vscode/devcontainers/java:11

# Install Node.js and npm
RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash - \
    && apt-get install -y nodejs

# Install Vue CLI
RUN npm install -g @vue/cli

# Install additional dependencies
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends postgresql-client

# Clean up
RUN apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set the default shell to bash
ENV SHELL /bin/bash 