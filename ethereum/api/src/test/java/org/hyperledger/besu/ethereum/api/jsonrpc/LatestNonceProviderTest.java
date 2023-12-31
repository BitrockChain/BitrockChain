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
package org.hyperledger.besu.ethereum.api.jsonrpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.ethereum.api.query.BlockchainQueries;
import org.hyperledger.besu.ethereum.eth.transactions.PendingTransactions;

import java.util.OptionalLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LatestNonceProviderTest {

  private final Address senderAddress = Address.fromHexString("1");

  @Mock private BlockchainQueries blockchainQueries;
  private LatestNonceProvider nonceProvider;

  @Mock private PendingTransactions pendingTransactions;

  @Before
  public void setUp() {
    nonceProvider = new LatestNonceProvider(blockchainQueries, pendingTransactions);
  }

  @Test
  public void nextNonceUsesTxPool() {
    final long highestNonceInPendingTransactions = 123;
    when(pendingTransactions.getNextNonceForSender(senderAddress))
        .thenReturn(OptionalLong.of(highestNonceInPendingTransactions));
    assertThat(nonceProvider.getNonce(senderAddress)).isEqualTo(highestNonceInPendingTransactions);
  }

  @Test
  public void nextNonceIsTakenFromBlockchainIfNoPendingTransactionResponse() {
    final long headBlockNumber = 8;
    final long nonceInBlockchain = 56;
    when(pendingTransactions.getNextNonceForSender(senderAddress)).thenReturn(OptionalLong.empty());
    when(blockchainQueries.headBlockNumber()).thenReturn(headBlockNumber);
    when(blockchainQueries.getTransactionCount(senderAddress, headBlockNumber))
        .thenReturn(nonceInBlockchain);
    assertThat(nonceProvider.getNonce(senderAddress)).isEqualTo(nonceInBlockchain);
  }
}
