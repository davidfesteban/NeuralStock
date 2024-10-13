//TODO: @GetMapping("/save & upload DataSet")
//TODO: Backup Scheduled for Networks & load
//TODO: Clean up Network static
//TODO: Allow Algorithm modifications


# Backpropagation Example

This document walks through the process of forward propagation, calculating the gradients of the loss function with respect to weights and biases, and updating them during backpropagation. The example includes a neural network with two hidden layers and two output neurons.

## Step 1: Recap of the Forward Pass

We already computed the following during the forward pass:

- **Activations of the first hidden layer**:
    - $\( a_{1,1} = 0.6457 \) (neuron \( H_1 \))$
    - $\( a_{1,2} = 0.3775 \) (neuron \( H_2 \))$

- **Activations of the second hidden layer**:
    - $\( a_{2,1} = 0.5657 \) (neuron \( H_{2,1} \))$
    - $\( a_{2,2} = 0.4979 \) (neuron \( H_{2,2} \))$

- **Activations of the output layer**:
    - $\( a_{3,1} = 0.5532 \) (neuron \( O_1 \))$
    - $\( a_{3,2} = 0.6321 \) (neuron \( O_2 \))$

- **Target outputs**:
    - $\( y_1 = 0.8 \) for output \( O_1 \)$
    - $\( y_2 = 0.4 \) for output \( O_2 \)$

- **Loss function** (MSE):

```math
L = \frac{1}{2} \left( (0.5532 - 0.8)^2 + (0.6321 - 0.4)^2 \right) = \frac{1}{2} \times 0.061 = 0.0305
```

## Step 2: Backpropagation

Now, we perform backpropagation to adjust the weights and biases. We will calculate the gradients layer by layer using the chain rule.

### a. Gradients for the output layer (for weights $\( w_{3,11}, w_{3,12}, w_{3,21}, w_{3,22} \$))

We will calculate the gradients of the loss with respect to the weights between the second hidden layer and the output layer.

#### For $\( O_1 \)$:

1. **Gradient of the loss with respect to the output activation $\( a_{3,1} \)$**:

$\[
\frac{\partial L}{\partial a_{3,1}} = a_{3,1} - y_1 = 0.5532 - 0.8 = -0.2468
\]$

2. **Derivative of the sigmoid function for $\( z_{3,1} \)$**:

$\[
\frac{\partial a_{3,1}}{\partial z_{3,1}} = a_{3,1} \times (1 - a_{3,1}) = 0.5532 \times (1 - 0.5532) = 0.2471
\]$

3. **Gradient of the loss with respect to $\( z_{3,1} \)$**:

$\[
\frac{\partial L}{\partial z_{3,1}} = \frac{\partial L}{\partial a_{3,1}} \times \frac{\partial a_{3,1}}{\partial z_{3,1}} = -0.2468 \times 0.2471 \approx -0.0610
\]$

4. **Gradients for the weights $\( w_{3,11} \)$ and $\( w_{3,21} \)$** (for neuron $\( O_1 \)$):

$\[
\frac{\partial L}{\partial w_{3,11}} = \frac{\partial L}{\partial z_{3,1}} \times a_{2,1} = -0.0610 \times 0.5657 \approx -0.0345
\]$

$\[
\frac{\partial L}{\partial w_{3,21}} = \frac{\partial L}{\partial z_{3,1}} \times a_{2,2} = -0.0610 \times 0.4979 \approx -0.0304
\]$

5. **Gradient for the bias $\( b_{3,1} \)$ (for neuron $\( O_1 \)$)**:

$\[
\frac{\partial L}{\partial b_{3,1}} = \frac{\partial L}{\partial z_{3,1}} = -0.0610
\]$


### b. Gradients for the second hidden layer (for weights $\( w_{2,11}, w_{2,12}, w_{2,21}, w_{2,22} \)$)

Now we calculate the gradients for the second hidden layer. These gradients are affected by both output neurons \( O_1 \) and \( O_2 \).

#### For $\( H_{2,1} \)$:

1. **Gradient of the loss with respect to $\( z_{2,1} \)$** (combining gradients from both output neurons):

$\[
\frac{\partial L}{\partial z_{2,1}} = \left( \frac{\partial L}{\partial z_{3,1}} \times w_{3,11} \right) + \left( \frac{\partial L}{\partial z_{3,2}} \times w_{3,12} \right)
\]$

$\[
\frac{\partial L}{\partial z_{2,1}} = (-0.0610 \times 0.6) + (0.0539 \times 0.7) = -0.0366 + 0.0377 = 0.0011
\]$

2. **Derivative of the sigmoid function for $\( z_{2,1} \)$**:

$\[
\frac{\partial a_{2,1}}{\partial z_{2,1}} = a_{2,1} \times (1 - a_{2,1}) = 0.5657 \times (1 - 0.5657) = 0.2456
\]$

3. **Gradient of the loss with respect to $\( z_{2,1} \)$**:

$\[
\frac{\partial L}{\partial z_{2,1}} = 0.0011 \times 0.2456 = 0.0003
\]$

4. **Gradients for the weights $\( w_{2,11} \)$ and $\( w_{2,12} \)$**:

$\[
\frac{\partial L}{\partial w_{2,11}} = \frac{\partial L}{\partial z_{2,1}} \times a_{1,1} = 0.0003 \times 0.6457 = 0.0002
\]$

$\[
\frac{\partial L}{\partial w_{2,12}} = \frac{\partial L}{\partial z_{2,1}} \times a_{1,2} = 0.0003 \times 0.3775 = 0.0001
\]$

5. **Gradient for the bias $\( b_{2,1} \)$**:

$\[
\frac{\partial L}{\partial b_{2,1}} = 0.0003
\]$

## Step 3: Update Weights and Biases

Using gradient descent, we update the weights and biases. Assume the learning rate $\( \eta = 0.1 \)$.

### Update for output layer:

$\[
w_{3,11} \leftarrow w_{3,11} - \eta \times \frac{\partial L}{\partial w_{3,11}} = 0.6 - 0.1 \times (-0.0345) = 0.6035
\]$

$\[
w_{3,21} \leftarrow w_{3,21} - \eta \times \frac{\partial L}{\partial w_{3,21}} = -0.5 - 0.1 \times (-0.0304) = -0.4970
\]$

$\[
w_{3,12} \leftarrow w_{3,12} - \eta \times \frac{\partial L}{\partial w_{3,12}} = 0.7 - 0.1 \times 0.0305 = 0.6969
\]$

$\[
w_{3,22} \leftarrow w_{3,22} - \eta \times \frac{\partial L}{\partial w_{3,22}} = 0.4 - 0.1 \times 0.0268 = 0.3973
\]$

### Update for hidden layer 2:

$\[
w_{2,11} \leftarrow w_{2,11} - \eta \times \frac{\partial L}{\partial w_{2,11}} = 0.2 - 0.1 \times 0.0002 = 0.19998
\]$

$\[
w_{2,12} \leftarrow w_{2,12} - \eta \times \frac{\partial L}{\partial w_{2,12}} = 0.3 - 0.1 \times 0.0001 = 0.29999
\]$

$\[
w_{2,21} \leftarrow w_{2,21} - \eta \times \frac{\partial L}{\partial w_{2,21}} = -0.2 - 0.1 \times 0.0084 = -0.20084
\]$

$\[
w_{2,22} \leftarrow w_{2,22} - \eta \times \frac{\partial L}{\partial w_{2,22}} = 0.4 - 0.1 \times 0.0049 = 0.39951
\]$

---

Have fun.