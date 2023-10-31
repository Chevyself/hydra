package me.googas.hydra.io;

import lombok.RequiredArgsConstructor;
import me.googas.hydra.ServiceMessenger;

@RequiredArgsConstructor
public abstract class MessageIO {
    protected final ServiceMessenger messenger;
}
