import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { BanquitoClient } from './banquito.client.js';

/**
 * REST client of the BanQuito Core (S5-S9).
 *
 * It only wraps the bank: the invoicing module is what decides when to call
 * it (S11) and how to answer the seller when the bank rejects or is down.
 */
@Module({
  imports: [ConfigModule],
  providers: [BanquitoClient],
  exports: [BanquitoClient],
})
export class BanquitoModule {}
