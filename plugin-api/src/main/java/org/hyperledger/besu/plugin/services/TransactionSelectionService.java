/*
 * Copyright Hyperledger Besu Contributors.
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

package org.hyperledger.besu.plugin.services;

import org.hyperledger.besu.plugin.Unstable;
import org.hyperledger.besu.plugin.services.txselection.TransactionSelectorFactory;

import java.util.Optional;

/** Transaction selection service interface */
@Unstable
public interface TransactionSelectionService extends BesuService {

  /**
   * Returns the (Optional) transaction selector factory
   *
   * @return the transaction selector factory
   */
  Optional<TransactionSelectorFactory> get();

  /**
   * Registers the transaction selector factory with the service
   *
   * @param transactionSelectorFactory transaction selector factory to be used
   */
  void registerTransactionSelectorFactory(TransactionSelectorFactory transactionSelectorFactory);
}
