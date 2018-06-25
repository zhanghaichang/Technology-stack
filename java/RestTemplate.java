@Bean
    public RestTemplate buildRestTemplate(List<CustomHttpRequestInterceptor> interceptors) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    HttpComponentsClientHttpRequestFactory factory = new                                                    
        HttpComponentsClientHttpRequestFactory();
    factory.setConnectionRequestTimeout(requestTimeout);
    factory.setConnectTimeout(connectTimeout);
    factory.setReadTimeout(readTimeout);
    // https
    SSLContextBuilder builder = new SSLContextBuilder();
    builder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true);
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", new PlainConnectionSocketFactory())
            .register("https", socketFactory).build();
    PoolingHttpClientConnectionManager phccm = new PoolingHttpClientConnectionManager(registry);
    phccm.setMaxTotal(200);
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).setConnectionManager(phccm).setConnectionManagerShared(true).build();
    factory.setHttpClient(httpClient);

    RestTemplate restTemplate = new RestTemplate(factory);
    List<ClientHttpRequestInterceptor> clientInterceptorList = new ArrayList<>();
    for (CustomHttpRequestInterceptor i : interceptors) {
        ClientHttpRequestInterceptor interceptor = i;
        clientInterceptorList.add(interceptor);
    }
    restTemplate.setInterceptors(clientInterceptorList);
    
    return restTemplate;
}
