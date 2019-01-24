package com.mdud.bathymetryplatform.repository;

public interface NativePersister<T> {
    T nativeSave(T entity);
}
