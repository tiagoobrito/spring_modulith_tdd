library(readr)

resultsGETMONOLITH <- read.csv("MonolithGET.csv")
View(resultsGETMONOLITH)

resultsGETMODULITH <- read.csv("ModulithAsyncGET.csv")
View(resultsGETMODULITH)

resultsPOSTMONOLITH <- read.csv("MonolithPOST.csv")
View(resultsPOSTMONOLITH)

resultsPOSTMODULITH <- read.csv("ModulithAsyncPOST.csv")
View(resultsPOSTMODULITH)

resultsSyncGETMODULITH <- read.csv("ModulithSyncGET.csv")
View(resultsSyncGETMODULITH)

resultsSyncPostMODULITH <- read.csv("ModulithSyncPost.csv")
View(resultsSyncPostMODULITH)


ConjuntoaverageTimeGetMonolith <- resultsGETMONOLITH$elapsed
ConjuntoaverageTimeGetModulith <- resultsGETMODULITH$elapsed
ConjuntoaverageTimePostMonolith <- resultsPOSTMONOLITH$elapsed
ConjuntoaverageTimePostModulith <- resultsPOSTMODULITH$elapsed
ConjuntoaverageTimeGetSyncModulith <- resultsSyncGETMODULITH$elapsed
ConjuntoaverageTimePostSyncModulith<-resultsSyncPostMODULITH$elapsed

mean(ConjuntoaverageTimePostMonolith)
mean(ConjuntoaverageTimePostModulith)
mean(ConjuntoaverageTimePostSyncModulith)
mean(ConjuntoaverageTimeGetMonolith)
mean(ConjuntoaverageTimeGetModulith)
mean(ConjuntoaverageTimeGetSyncModulith)

median(ConjuntoaverageTimePostMonolith)
median(ConjuntoaverageTimePostModulith)
median(ConjuntoaverageTimePostSyncModulith)
median(ConjuntoaverageTimeGetMonolith)
median(ConjuntoaverageTimeGetModulith)
median(ConjuntoaverageTimeGetSyncModulith)

max(ConjuntoaverageTimePostMonolith)
max(ConjuntoaverageTimePostModulith)
max(ConjuntoaverageTimePostSyncModulith)
max(ConjuntoaverageTimeGetMonolith)
max(ConjuntoaverageTimeGetModulith)
max(ConjuntoaverageTimeGetSyncModulith)


#Neste teste de hipóteses considera-se 0.05 como o nível de significância

#TESTE DE NORMALIDADE
library(nortest) #Teste de lilliefor porque são 3000 valores

lillie.test(ConjuntoaverageTimeGetMonolith)
lillie.test(ConjuntoaverageTimeGetModulith)
lillie.test(ConjuntoaverageTimePostMonolith)
lillie.test(ConjuntoaverageTimePostModulith)
lillie.test(ConjuntoaverageTimeGetSyncModulith)
lillie.test(ConjuntoaverageTimePostSyncModulith)

#Hipótese nula (H0): Não há diferença significativa no desempenho das duas arquiteturas
#Hipótese alternativa (H1): Há diferença significativa no desempenho das duas arquiteturas


valor_p_POST_MONO_ASYNC <- wilcox.test(ConjuntoaverageTimePostMonolith,ConjuntoaverageTimePostModulith, paired=FALSE)
valor_p_POST_MONO_ASYNC

valor_p_GET_MONO_ASYNC <- wilcox.test(ConjuntoaverageTimeGetMonolith,ConjuntoaverageTimeGetModulith, paired=FALSE)
valor_p_GET_MONO_ASYNC

valor_p_POST_MONO_SYNC <- wilcox.test(ConjuntoaverageTimePostMonolith,ConjuntoaverageTimePostSyncModulith, paired=FALSE)
valor_p_POST_MONO_SYNC

valor_p_GET_MONO_SYNC <- wilcox.test(ConjuntoaverageTimeGetMonolith,ConjuntoaverageTimeGetSyncModulith, paired=FALSE)
valor_p_GET_MONO_SYNC

#Como p-value é praticamente 0 então temos evidências estatisticas de que há diferença significativa no desempenho das duas arquiteturas (Rejeitando-se a Hipótese nula)

valor_p_GET_ASYNC_SYNC <- wilcox.test(ConjuntoaverageTimeGetSyncModulith,ConjuntoaverageTimeGetModulith, paired=FALSE)
valor_p_GET_ASYNC_SYNC

#Como p-value é maior que 0.05 temos evidências estatísticas de que não há diferença significativa no desempenho dos dois protótipos (Não se rejeita a Hipótese nula)

valor_p_POST_ASYNC_SYNC <- wilcox.test(ConjuntoaverageTimePostSyncModulith,ConjuntoaverageTimePostModulith, paired=FALSE)
valor_p_POST_ASYNC_SYNC

#Como p-value é praticamente 0 então temos evidências estatisticas de que há diferença significativa no desempenho dos dois protótipos (Rejeitando-se a Hipótese nula)



# -------------------------------------------------------------------------------------------------------------------------------

# Testes de Stress

resultsGETMONOLITHst <- read.csv("5000GETMonolith.csv")
View(resultsGETMONOLITHst)

resultsGETMODULITHst <- read.csv("5000GETAsync.csv")
View(resultsGETMODULITHst)

resultsPOSTMONOLITHst <- read.csv("5000MonolithPOST.csv")
View(resultsPOSTMONOLITHst)

resultsPOSTMODULITHst <- read.csv("5000AsyncPOST.csv")
View(resultsPOSTMODULITHst)

resultsSyncGETMODULITHst <- read.csv("5000GETSync.csv")
View(resultsSyncGETMODULITHst)

resultsSyncPostMODULITHst <- read.csv("5000SyncPOST.csv")
View(resultsSyncPostMODULITHst)

ConjuntoaverageTimeGetModulithst <- resultsGETMODULITHst$elapsed
ConjuntoaverageTimeGetSyncModulithst <- resultsSyncGETMODULITHst$elapsed



ConjuntoaverageTimeGetMonolithst <- resultsGETMONOLITHst$elapsed
ConjuntoaverageTimeGetModulithst <- resultsGETMODULITHst$elapsed
ConjuntoaverageTimePostMonolithst <- resultsPOSTMONOLITHst$elapsed
ConjuntoaverageTimePostModulithst <- na.omit(resultsPOSTMODULITHst$elapsed)
ConjuntoaverageTimeGetSyncModulithst <- resultsSyncGETMODULITHst$elapsed
ConjuntoaverageTimePostSyncModulithst <-resultsSyncPostMODULITHst$elapsed


mean(ConjuntoaverageTimeGetMonolithst)
mean(ConjuntoaverageTimeGetModulithst)
mean(ConjuntoaverageTimeGetSyncModulithst)
mean(ConjuntoaverageTimePostMonolithst)
mean(ConjuntoaverageTimePostModulithst)
mean(ConjuntoaverageTimePostSyncModulithst)

library(nortest) #Teste de lilliefor porque são 15000 valores

lillie.test(ConjuntoaverageTimeGetMonolithst)
lillie.test(ConjuntoaverageTimeGetModulithst)
lillie.test(ConjuntoaverageTimePostMonolithst)
lillie.test(ConjuntoaverageTimePostModulithst)
lillie.test(ConjuntoaverageTimeGetSyncModulithst)
lillie.test(ConjuntoaverageTimePostSyncModulithst)

#Hipótese nula (H0): Não há diferença significativa no desempenho das duas arquiteturas
#Hipótese alternativa (H1): Há diferença significativa no desempenho das duas arquiteturas


valor_p_POST_ASYNC_MONO <- wilcox.test(ConjuntoaverageTimePostMonolithst,ConjuntoaverageTimePostModulithst, paired=FALSE)
valor_p_POST_ASYNC_MONO

valor_p_GET_ASYNC_MONO <- wilcox.test(ConjuntoaverageTimeGetMonolithst,ConjuntoaverageTimeGetModulithst, paired=FALSE)
valor_p_GET_ASYNC_MONO

valor_p_POST_SYNC_MONO <- wilcox.test(ConjuntoaverageTimePostMonolithst,ConjuntoaverageTimePostSyncModulithst, paired = FALSE)
valor_p_POST_SYNC_MONO

valor_p_GET_SYNC_MONO <- wilcox.test(ConjuntoaverageTimeGetMonolithst,ConjuntoaverageTimeGetSyncModulithst, paired = FALSE)
valor_p_POST_SYNC_MONO

valor_p_GET_SYNC_ASYNC <- wilcox.test(ConjuntoaverageTimeGetSyncModulithst,ConjuntoaverageTimeGetModulithst, paired=FALSE)
valor_p_GET_SYNC_ASYNC

valor_p_POST_SYNC_ASYNC <- wilcox.test(ConjuntoaverageTimePostSyncModulithst,ConjuntoaverageTimePostModulithst, paired=FALSE)
valor_p_POST_SYNC_ASYNC

#Como p-value é praticamente 0 então temos evidências estatisticas de que há diferença significativa no desempenho dos protótipos (Rejeitando-se a Hipótese nula)









#GRÁFICO DE BARRAS 

#POST-Average

dados <- c(3.64,17.29,22.12)
categorias <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")

#cores para as barras

cores <- c("orange","blue", "green")

# Criar o gráfico de barras com cores diferentes
grafico <- barplot(dados, names.arg = categorias, xlab = "Média", ylab = "Tempo de Resposta (ms)", col = cores,ylim = c(0, max(dados) + 10))

# Adicionar números acima das barras
text(x = grafico, y = dados + 5, labels = dados, pos = 3, col = "black")


#Throughtput


dadosThroughtput <- c(303.7,303.6,303.1)
categoriasThroughtput <- c("Monolith",  "Modulith (Async)", "Modulith (Sync)")

# Criar o gráfico de barras com cores diferentes
grafico <- barplot(dadosThroughtput, names.arg = categoriasThroughtput, xlab = "Throughput", ylab = "Pedidos por segundo", col = cores,ylim = c(0, max(dadosThroughtput) + 80))

# Adicionar números acima das barras
text(x = grafico, y = dadosThroughtput + 20, labels = dadosThroughtput, pos = 3, col = "black")


##GET

dadosGET <- c(2.28,2.89,2.91)
categoriasGET <- c("Monolith","Modulith (Async)", "Modulith (Sync)")

# Criar o gráfico de barras com cores diferentes
grafico <- barplot(dadosGET, names.arg = categoriasGET, xlab = "Média", ylab = "Tempo de Resposta (ms)", col = cores,ylim = c(0, max(dadosGET) + 2))

# Adicionar números acima das barras
text(x = grafico, y = dadosGET + 1, labels = dadosGET, pos = 3, col = "black")


##Throughtput

dadost <- c(300.1,300.0,300.0)
categoriast <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")

# Criar o gráfico de barras com cores diferentes
grafico <- barplot(dadost, names.arg = categoriast, xlab = "Throughput", ylab = "Pedidos por segundo", col = cores,ylim = c(0, max(dadost) + 50))

# Adicionar números acima das barras
text(x = grafico, y = dadost + 1, labels = dadost, pos = 3, col = "black")



#Stress


#GET
dadosgetstresstempo <- c(756.7,840.6,898.2)
categoriast <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")

# Criar o gráfico de barras com cores diferentes
grafico <- barplot(dadosgetstresstempo, names.arg = categoriast, xlab = "Média", ylab = "Tempo de Resposta (ms)", col = cores,ylim = c(0, max(dadosgetstresstempo) + 500))

# Adicionar números acima das barras
text(x = grafico, y = dadosgetstresstempo + 1, labels = dadosgetstresstempo, pos = 3, col = "black")
  
#ERROR(%)

dadosgetstresserror <- c(14.11,15.52,17.87)
categoriast <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")
grafico <- barplot(dadosgetstresserror, names.arg = categoriast, xlab = "Taxa de Erro", ylab = "Taxa de Erro (%)", col = cores,ylim = c(0, max(dadosgetstresserror) + 10))
text(x = grafico, y = dadosgetstresserror + 1, labels = dadosgetstresserror, pos = 3, col = "black")


#POST

dadospoststresstempo <- c(1183.3,1624.9,4797.6)
categoriast <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")
grafico <- barplot(dadospoststresstempo, names.arg = categoriast, xlab = "Média", ylab = "Taxa de Erro (%)", col = cores,ylim = c(0, max(dadospoststresstempo) + 2000))
text(x = grafico, y = dadospoststresstempo + 1, labels = dadospoststresstempo, pos = 3, col = "black")

#ERROR(%)

dadospoststresserror <- c(23.56,44.93,70.47)
categoriast <- c("Monolith", "Modulith (Async)", "Modulith (Sync)")
grafico <- barplot(dadospoststresserror, names.arg = categoriast, xlab = "Taxa de Erro", ylab = "Taxa de Erro (%)", col = cores,ylim = c(0, max(dadospoststresserror) + 10))
text(x = grafico, y = dadospoststresserror + 1, labels = dadospoststresserror, pos = 3, col = "black")









