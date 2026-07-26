import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs';

export const apiResponseInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    map(event => {
      if (event instanceof HttpResponse && event.body && typeof event.body === 'object' && 'success' in event.body && 'data' in event.body) {
        const body = event.body as { success: boolean; data: any; message?: string; timestamp?: string };
        if (body.success === false) {
          return event.clone({ body: { success: false, message: body.message, data: null } });
        }
        return event.clone({ body: body.data !== undefined ? body.data : body });
      }
      return event;
    })
  );
};
