from array import array
import numpy as np
from Bio import Cluster as pc
import os
import csv
import argparse

"""https://www.dataquest.io/blog/machine-learning-python/"""

def load_aspects(filename):
    aspects = []
    fid = open(filename, "r")
    for line in fid:
        line = line.strip()
        aspects.append(line)
    fid.close()

    return aspects

# full distance matrix
def get_distance_matrix(aspects, filename):
    """load distance matrix for aspects"""
    #read similarity matrix file
    fid = open(filename, "r")

    #prepare similarity array
    similarity = np.array([])

    #create rows
    #get first line of matrix in fid & split into new array
    firstline = True

    for counter,line in enumerate(fid):
        temp = np.array([]);
        data = line.split(',', len(aspects))
        #create columns in rows
        for j in data:
            sim = float(j)
            temp = np.hstack((temp, sim));
            #temp.append(sim)
        #append row

        if (firstline):
            firstline = False
            similarity = temp
        else:
            similarity = np.vstack((similarity, temp));

        #similarity.append(temp)
        #similarity = numpy.append(similarity, temp, axis=0)



    return np.array(similarity)


# create csv file with cluster data
def write_cluster_csv(aspects, clusterid, csv_filename):
    with open(csv_filename, 'w+') as fout:
        csv_file = csv.writer(fout)
        #csv_file.writerow(list(column_names)) no headerline
        for counter, aspect in enumerate(aspects):
            csv_file.writerow([aspect, clusterid[counter]])

def output_clusters(aspects, clusterid):
    assignments = {}
    for counter, aspect in enumerate(aspects):
        tmp_clu_id = clusterid[counter]

        if tmp_clu_id in assignments:
            new_list = assignments[tmp_clu_id]
            new_list.append(aspect)
        else:
            new_list = []
            assignments[tmp_clu_id] = new_list
            new_list.append(aspect)

    counter = 1
    for cluster_id, cluster_list in assignments.iteritems():
        cluster_name = aspects[cluster_id]
        print cluster_name

        for asp in cluster_list:
            print asp

        print "\n"

        counter = counter + 1

if __name__ == '__main__':

    #read command line argument --> share of clusters

    parser = argparse.ArgumentParser(
        description='number of clusters',
    )
    parser.add_argument(
        'nclusters',
        type=int,
        help='number of clusters.',
    )
    args = parser.parse_args()
    nclusters = args.nclusters

    filename = "data/output/aspectsDbscanFiltered.txt"
    matrixFilename = "data/output/distancesDbscanFiltered.txt"
    clusterFilename = "data/clusteringresult/aspectClusterDbscanFiltered.csv"

    aspects = load_aspects(os.getcwd() + "/" + filename)
    distances = get_distance_matrix(aspects, os.getcwd() + "/" + matrixFilename)


    clusterid, error, nfound = pc.kmedoids(distances, nclusters=nclusters, npass=50, initialid=None)


    write_cluster_csv(aspects, clusterid, os.getcwd() + "/" + clusterFilename)

    output_clusters(aspects, clusterid)

    print "File written!"

