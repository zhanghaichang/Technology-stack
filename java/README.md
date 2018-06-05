

```java
	@SuppressWarnings("unchecked")
	private void checkParam(Context context, Map<String, Object> result, Map<String, Object> input, String... names) {
		if (log.isInfoEnabled()) {
			log.info("抽取结果检测"+NODE_INFO + "(" + ts.get() + ")");
		}
		
		for (String name : names) {
			if (result.get(name) == null || ((LinkedList<Object>) result.get(name)).isEmpty() || ((LinkedList<Object>) result.get(name)).get(0) == null) {
				printSystemException(context, OpasError.INVOKE_DATA_EXTRACT_ERROR.getErrorCode(), "数据抽取结果异常，不包含[" + name + "]", null);
			}

			Object card = ((LinkedList<Object>) result.get(name)).get(0);

			if (card == null || card.toString().trim().length() == 0 || card.toString().equals("null")) {
				printSystemException(context, OpasError.INVOKE_DATA_EXTRACT_ERROR.getErrorCode(), "数据抽取结果异常，[" + name + "]为空", null);
			} else {
				input.put(name, ((LinkedList<Object>) result.get(name)).get(0).toString().trim());
			}
		}
	}
```
