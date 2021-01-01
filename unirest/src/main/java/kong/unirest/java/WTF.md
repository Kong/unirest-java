## Try and describe what is happening here:


Class Structure:
 MultipartBoundaryPublisher.Builder -> gathers all the parts and builds a:

 MultipartBoundaryPublisher
   * Is the BodyPublisher (java) sent to the Http Client
      * A BodyPublisher creates a stream of bytebuffers that can be subscribed to by subscribe(Subscriber<? super T> subscriber);
    

