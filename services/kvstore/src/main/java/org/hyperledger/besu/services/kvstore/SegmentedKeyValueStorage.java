/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.services.kvstore;

import org.hyperledger.besu.plugin.services.exception.StorageException;
import org.hyperledger.besu.plugin.services.storage.SegmentIdentifier;

import java.io.Closeable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Service provided by Besu to facilitate persistent data storage.
 *
 * @param <S> the segment identifier type
 */
public interface SegmentedKeyValueStorage<S> extends Closeable {

  /**
   * Gets segment identifier by name.
   *
   * @param segment the segment
   * @return the segment identifier by name
   */
  S getSegmentIdentifierByName(SegmentIdentifier segment);

  /**
   * Get the value from the associated segment and key.
   *
   * @param segment the segment
   * @param key Index into persistent data repository.
   * @return The value persisted at the key index.
   * @throws StorageException the storage exception
   */
  Optional<byte[]> get(S segment, byte[] key) throws StorageException;

  /**
   * Contains key.
   *
   * @param segment the segment
   * @param key the key
   * @return the boolean
   * @throws StorageException the storage exception
   */
  default boolean containsKey(final S segment, final byte[] key) throws StorageException {
    return get(segment, key).isPresent();
  }

  /**
   * Begins a transaction. Returns a transaction object that can be updated and committed.
   *
   * @return An object representing the transaction.
   * @throws StorageException the storage exception
   */
  Transaction<S> startTransaction() throws StorageException;

  /**
   * Returns a stream of all keys for the segment.
   *
   * @param segmentHandle The segment handle whose keys we want to stream.
   * @return A stream of all keys in the specified segment.
   */
  Stream<Pair<byte[], byte[]>> stream(final S segmentHandle);

  /**
   * Returns a stream of key-value pairs starting from the specified key. This method is used to
   * retrieve a stream of data from the storage, starting from the given key. If no data is
   * available from the specified key onwards, an empty stream is returned.
   *
   * @param segmentHandle The segment handle whose keys we want to stream.
   * @param startKey The key from which the stream should start.
   * @return A stream of key-value pairs starting from the specified key.
   */
  Stream<Pair<byte[], byte[]>> streamFromKey(final S segmentHandle, final byte[] startKey);

  /**
   * Stream keys.
   *
   * @param segmentHandle the segment handle
   * @return the stream
   */
  Stream<byte[]> streamKeys(final S segmentHandle);

  /**
   * Delete the value corresponding to the given key in the given segment if a write lock can be
   * instantly acquired on the underlying storage. Do nothing otherwise.
   *
   * @param segmentHandle The segment handle whose keys we want to stream.
   * @param key The key to delete.
   * @return false if the lock on the underlying storage could not be instantly acquired, true
   *     otherwise
   * @throws StorageException any problem encountered during the deletion attempt.
   */
  boolean tryDelete(S segmentHandle, byte[] key) throws StorageException;

  /**
   * Gets all keys that matches condition.
   *
   * @param segmentHandle the segment handle
   * @param returnCondition the return condition
   * @return set of result
   */
  Set<byte[]> getAllKeysThat(S segmentHandle, Predicate<byte[]> returnCondition);

  /**
   * Gets all values from keys that matches condition.
   *
   * @param segmentHandle the segment handle
   * @param returnCondition the return condition
   * @return the set of result
   */
  Set<byte[]> getAllValuesFromKeysThat(final S segmentHandle, Predicate<byte[]> returnCondition);

  /**
   * Clear.
   *
   * @param segmentHandle the segment handle
   */
  void clear(S segmentHandle);

  /**
   * Whether the underlying storage is closed.
   *
   * @return boolean indicating whether the underlying storage is closed.
   */
  boolean isClosed();

  /**
   * Represents a set of changes to be committed atomically. A single transaction is not
   * thread-safe, but multiple transactions can execute concurrently.
   *
   * @param <S> the segment identifier type
   */
  interface Transaction<S> {

    /**
     * Add the given key-value pair to the set of updates to be committed.
     *
     * @param segment the database segment
     * @param key The key to set / modify.
     * @param value The value to be set.
     */
    void put(S segment, byte[] key, byte[] value);

    /**
     * Schedules the given key to be deleted from storage.
     *
     * @param segment the database segment
     * @param key The key to delete
     */
    void remove(S segment, byte[] key);

    /**
     * Atomically commit the set of changes contained in this transaction to the underlying
     * key-value storage from which this transaction was started. After committing, the transaction
     * is no longer usable and will throw exceptions if modifications are attempted.
     *
     * @throws StorageException the storage exception
     */
    void commit() throws StorageException;

    /**
     * Cancel this transaction. After rolling back, the transaction is no longer usable and will
     * throw exceptions if modifications are attempted.
     */
    void rollback();
  }
}
