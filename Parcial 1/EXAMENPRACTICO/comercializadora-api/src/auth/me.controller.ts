import { Controller, Get } from '@nestjs/common';
import { Session, type UserSession } from '@thallesp/nestjs-better-auth';

export interface MeResponse {
  id: string;
  name: string;
  email: string;
}

/**
 * Session probe for the Next.js app. No `@AllowAnonymous()` here: the global
 * Better Auth guard rejects the request with 401 when there is no session.
 */
@Controller('api')
export class MeController {
  @Get('me')
  me(@Session() session: UserSession): MeResponse {
    return {
      id: session.user.id,
      name: session.user.name,
      email: session.user.email,
    };
  }
}
