import { Injectable, Logger, type OnApplicationBootstrap } from '@nestjs/common';
import { InjectDataSource } from '@nestjs/typeorm';
import { AuthService } from '@thallesp/nestjs-better-auth';
import { DataSource } from 'typeorm';
import type { Auth } from './auth.js';

/** Demo seller the exam uses to sign in to the retailer app. */
export const DEMO_USER = {
  name: 'Vendedor Demo',
  email: 'vendedor@comercializadora.ec',
  password: 'Vendedor2026!',
} as const;

/**
 * Seeds the demo user on bootstrap, through the Better Auth API so the
 * password is hashed exactly like a real sign up. Idempotent: it only runs
 * when the email is not in the `user` table yet.
 */
@Injectable()
export class DemoUserSeed implements OnApplicationBootstrap {
  private readonly logger = new Logger(DemoUserSeed.name);

  constructor(
    private readonly authService: AuthService<Auth>,
    @InjectDataSource() private readonly dataSource: DataSource,
  ) {}

  async onApplicationBootstrap(): Promise<void> {
    const existing: { id: string }[] = await this.dataSource.query(
      'select "id" from "user" where "email" = $1 limit 1',
      [DEMO_USER.email],
    );

    if (existing.length > 0) {
      return;
    }

    await this.authService.api.signUpEmail({ body: { ...DEMO_USER } });
    this.logger.log(`Seeded demo user ${DEMO_USER.email}`);
  }
}
