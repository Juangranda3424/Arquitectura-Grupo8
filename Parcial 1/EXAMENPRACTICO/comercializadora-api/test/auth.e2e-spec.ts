import type { INestApplication } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import request from 'supertest';
import { AppModule } from '../src/app.module.js';

/**
 * Integration tests for the Better Auth setup (S15).
 *
 * They run against the real PostgreSQL from docker-compose: the TypeORM
 * migrations create the auth tables on bootstrap and the seed adds one session
 * row per sign in. Nothing else in the exam database is touched.
 */
describe('Better Auth (real database)', () => {
  const demoUser = {
    email: 'vendedor@comercializadora.ec',
    password: 'Vendedor2026!',
  };

  let app: INestApplication;

  const cookieHeader = (response: request.Response): string => {
    const raw = response.headers['set-cookie'] as unknown as string | string[];
    const cookies = Array.isArray(raw) ? raw : [raw];
    return cookies.map((cookie) => cookie.split(';')[0]).join('; ');
  };

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleRef.createNestApplication({ bodyParser: false });
    await app.init();
  });

  afterAll(async () => {
    await app.close();
  });

  it('rejects GET /api/me without a session', async () => {
    const response = await request(app.getHttpServer()).get('/api/me');

    expect(response.status).toBe(401);
  });

  it('rejects a sign in with the wrong password', async () => {
    const response = await request(app.getHttpServer())
      .post('/api/auth/sign-in/email')
      .set('Origin', 'http://localhost:3000')
      .send({ email: demoUser.email, password: 'contrasena-incorrecta' });

    expect(response.status).toBe(401);
    expect(response.headers['set-cookie']).toBeUndefined();
  });

  it('signs in the seeded demo user and returns it from GET /api/me', async () => {
    const signIn = await request(app.getHttpServer())
      .post('/api/auth/sign-in/email')
      .set('Origin', 'http://localhost:3000')
      .send(demoUser);

    expect(signIn.status).toBe(200);
    expect(cookieHeader(signIn)).toContain('better-auth.session_token');

    const me = await request(app.getHttpServer())
      .get('/api/me')
      .set('Cookie', cookieHeader(signIn));

    expect(me.status).toBe(200);
    expect(me.body).toEqual({
      id: expect.any(String) as string,
      name: 'Vendedor Demo',
      email: demoUser.email,
    });
  });
});
